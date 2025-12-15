package com.example.piuda.Pin;

import com.example.piuda.Report.ReportRepository;
import com.example.piuda.Notify.NotifyRepository;
import com.example.piuda.Reserv.ReservRepository;
import com.example.piuda.ReportPhoto.ReportPhotoRepository;
import com.example.piuda.domain.DTO.PinResponseDTO;
import com.example.piuda.domain.Entity.Pin;
import com.example.piuda.domain.Entity.Report;
import com.example.piuda.domain.Entity.Trash;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PinService {
    private final PinRepository pinRepository;
    private final ReportRepository reportRepository;
    private final NotifyRepository notifyRepository;
    private final ReservRepository reservRepository;
    private final ReportPhotoRepository reportPhotoRepository;
    private final com.example.piuda.NotifyPhoto.NotifyPhotoRepository notifyPhotoRepository;

    public List<Pin> getAllPins() {
        return pinRepository.findAll();
    }

    // 일정 거리 내 존재하는 핀 반환 (있으면 Optional로 반환)
    public Optional<Pin> findNearbyPin(double x, double y, double distanceMeters) {
        List<Pin> pins = pinRepository.findAll();
        for (Pin pin : pins) {
            double dx = pin.getPinX() - x;
            double dy = pin.getPinY() - y;
            double dist = Math.sqrt(dx * dx + dy * dy) * 111_000; // 위도/경도 -> m 변환(간략)
            if (dist < distanceMeters) {
                return Optional.of(pin);
            }
        }
        return Optional.empty();
    }

    // Report에서 사용하는 핀 확보: 근처에 있으면 기존 핀 반환, 없으면 WHITE 새 핀 생성
    @Transactional
    public Pin addPinFromReportAndGet(double x, double y, double distanceMeters) {
        Optional<Pin> near = findNearbyPin(x, y, distanceMeters);
        if (near.isPresent()) return near.get();
        Pin pin = Pin.builder()
                .pinX(x)
                .pinY(y)
                .pinCreatedAt(LocalDateTime.now())
                .pinColor(Pin.PinColor.WHITE)
                .build();
        return pinRepository.save(pin);
    }
    // 제보 상태가 수락으로 변경된경우 핀추가, 해당 위치의 핀 이미 존재시 존재하고 있는 핀 반환(red 핀 생성)
    @Transactional
    public Pin addPinFromNotifyAndGet(double x, double y, double distanceMeters) {
        // 근처에 핀이 없으면 새로 생성 후 반환
        Optional<Pin> near = findNearbyPin(x, y, distanceMeters);
        if (near.isPresent()) return near.get();
        Pin pin = Pin.builder()
                .pinX(x)
                .pinY(y)
                .pinCreatedAt(LocalDateTime.now())
                .pinColor(Pin.PinColor.RED)
                .build();
        return pinRepository.save(pin);
    }
    // 초기 지도 로드용: 전체 핀 데이터(클라이언트 필터링용 요약 포함) 반환
    @Transactional
    public List<PinResponseDTO> getAllPinsForClient() {
        List<Pin> pins = pinRepository.findAll();
        return pins.stream().map(pin -> {
            // 1) 관련 Report 조회 및 집계
            List<Report> reports = reportRepository.findByPin(pin);
            var orgNames = reports.stream()
                    .map(Report::getReportName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            double totalKg = reports.stream()
                    .map(Report::getTrash)
                    .filter(Objects::nonNull)
                    .mapToDouble(t -> t.getTrashKg() != null ? t.getTrashKg() : 0.0)
                    .sum();
            double totalL = reports.stream()
                    .map(Report::getTrash)
                    .filter(Objects::nonNull)
                    .mapToDouble(t -> t.getTrashL() != null ? t.getTrashL() : 0.0)
                    .sum();
            int activityCount = reports.size();
            LocalDate latestActivityDate = reports.stream()
                    .map(Report::getReportDate)
                    .filter(Objects::nonNull)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            // 2) Report 요약 생성 (trashKg, trashL, 사진 경로 포함)
            List<PinResponseDTO.ReportSummary> reportSummaries = reports.stream().map(r -> {
                var photos = reportPhotoRepository.findByReport(r);
                List<String> photoPaths = photos.stream()
                        .map(p -> p.getRphotoPath())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                return new PinResponseDTO.ReportSummary(
                        r.getReportId(),
                        r.getReportTitle(),
                        r.getReportName(),
                        r.getReportDate(),
                        r.getTrash() != null ? r.getTrash().getTrashKg() : null,
                        r.getTrash() != null ? r.getTrash().getTrashL() : null,
                        r.getReportContent(),
                        photoPaths
                );
            }).collect(Collectors.toList());

        // 3) 빨간 핀(RED)인 경우 ACCEPT 상태 제보 + 사진 URL 포함
            List<PinResponseDTO.NotifySummary> notifySummaries = null;
            if (pin.getPinColor() == Pin.PinColor.RED) {
                var notifies = notifyRepository.findByPinAndNotifyStatus(pin, com.example.piuda.domain.Entity.Notify.NotifyStatus.ACCEPT);
                notifySummaries = notifies.stream().map(n -> {
                    var nPhotos = notifyPhotoRepository.findByNotify(n);
                    List<String> nPhotoUrls = nPhotos.stream()
                            .map(np -> np.getNphotoPath())
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    return new PinResponseDTO.NotifySummary(
                            n.getNotifyId(),
                            n.getNotifyContent(),
                            nPhotoUrls
                    );
                }).collect(Collectors.toList());
            }

        // 4) 파란 핀(BLUE)인 경우, 오늘 이후 예약만 포함
        List<PinResponseDTO.ReservSummary> reservSummaries = null;
        if (pin.getPinColor() == Pin.PinColor.BLUE) {
        LocalDate today = LocalDate.now();
        var reservs = reservRepository.findByPin(pin);
        reservSummaries = reservs.stream()
            .filter(rv -> rv.getReservDate() != null && rv.getReservDate().isAfter(today))
            .map(rv -> new PinResponseDTO.ReservSummary(
                rv.getReservId(),
                rv.getReservTitle(),
                rv.getReservOrg(),
                rv.getReservDate(),
                rv.getReservPeople()
            ))
            .collect(Collectors.toList());
        }

        // 5) 상세 형태 DTO 반환 (요약 요청에서도 동일 구조 유지)
            return PinResponseDTO.detailed(
                    pin,
                    orgNames,
                    totalKg,
                    totalL,
                    latestActivityDate,
                    activityCount,
            reportSummaries,
            notifySummaries,
            reservSummaries
            );
        }).collect(Collectors.toList());
    }

    // 핀 상세 조회 (PinResponseDTO 재사용)
    @Transactional
    public PinResponseDTO getPinDetails(Long pinId) {
        Pin pin = pinRepository.findById(pinId).orElse(null);
        if (pin == null) return null;

        List<Report> reports = reportRepository.findByPin(pin);
        var orgNames = reports.stream()
            .map(Report::getReportName)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        double totalKg = reports.stream()
            .map(Report::getTrash)
            .filter(Objects::nonNull)
            .mapToDouble(t -> t.getTrashKg() != null ? t.getTrashKg() : 0.0)
            .sum();
        double totalL = reports.stream()
            .map(Report::getTrash)
            .filter(Objects::nonNull)
            .mapToDouble(t -> t.getTrashL() != null ? t.getTrashL() : 0.0)
            .sum();
        int activityCount = reports.size();
        LocalDate latestActivityDate = reports.stream()
            .map(Report::getReportDate)
            .filter(Objects::nonNull)
            .max(LocalDate::compareTo)
            .orElse(null);

        List<PinResponseDTO.ReportSummary> summaries = reports.stream()
            .map(r -> {
                var photos = reportPhotoRepository.findByReport(r);
                List<String> paths = photos.stream()
                    .map(p -> p.getRphotoPath())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                return new PinResponseDTO.ReportSummary(
                    r.getReportId(),
                    r.getReportTitle(),
                    r.getReportName(),
                    r.getReportDate(),
                    r.getTrash() != null ? r.getTrash().getTrashKg() : null,
                    r.getTrash() != null ? r.getTrash().getTrashL() : null,
                    r.getReportContent(),
                    paths
                );
            })
            .collect(Collectors.toList());

    // 빨간 핀(RED)일 때만 해당 핀의 ACCEPT 제보 정보 포함
    List<PinResponseDTO.NotifySummary> notifySummaries = null;
    if (pin.getPinColor() == Pin.PinColor.RED) {
        var notifies = notifyRepository.findByPinAndNotifyStatus(pin, com.example.piuda.domain.Entity.Notify.NotifyStatus.ACCEPT);
        notifySummaries = notifies.stream().map(n -> {
        var photos = notifyPhotoRepository.findByNotify(n);
        List<String> photoUrls = photos.stream()
            .map(np -> np.getNphotoPath())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return new PinResponseDTO.NotifySummary(
            n.getNotifyId(),
            n.getNotifyContent(),
            photoUrls
        );
        }).collect(Collectors.toList());
    }

        // BLUE 핀: 오늘 이후 예약만 포함
        List<PinResponseDTO.ReservSummary> reservSummaries = null;
        if (pin.getPinColor() == Pin.PinColor.BLUE) {
            LocalDate today = LocalDate.now();
            var reservs = reservRepository.findByPin(pin);
            reservSummaries = reservs.stream()
                    .filter(rv -> rv.getReservDate() != null && rv.getReservDate().isAfter(today))
                    .map(rv -> new PinResponseDTO.ReservSummary(
                            rv.getReservId(),
                            rv.getReservTitle(),
                            rv.getReservOrg(),
                            rv.getReservDate(),
                            rv.getReservPeople()
                    ))
                    .collect(Collectors.toList());
        }

        return PinResponseDTO.detailed(
            pin,
            orgNames,
            totalKg,
            totalL,
            latestActivityDate,
            activityCount,
            summaries,
            notifySummaries,
            reservSummaries
        );
    }


    // 예약 시 핀 색상 파란색으로 변경
    public boolean setPinColorBlue(Long pinId) {
        return pinRepository.findById(pinId).map(pin -> {
            Pin updated = Pin.builder()
                .pinId(pin.getPinId())
                .pinX(pin.getPinX())
                .pinY(pin.getPinY())
                .pinCreatedAt(pin.getPinCreatedAt())
                .region(pin.getRegion())
                .pinColor(Pin.PinColor.BLUE)
                .build();
            pinRepository.save(updated);
            return true;
        }).orElse(false);
    }

    // RED 핀 중 생성 후 14일 지난 핀을 정리하되, Report/Reserv가 참조하지 않는 경우에만 삭제.
    // Notify가 참조 중이면 Notify.pin을 null로 세팅한 뒤 삭제한다.
    @Transactional
    public int cleanupOldRedPins() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(14);
        List<Pin> deletable = pinRepository.findDeletablePins(Pin.PinColor.RED, cutoff);
        int count = 0;
        for (Pin p : deletable) {
            // Notify 참조 끊기
            var notifies = notifyRepository.findByPin(p);
            if (!notifies.isEmpty()) {
                notifies.forEach(n -> n.setPin(null));
                notifyRepository.saveAll(notifies);
            }
            pinRepository.delete(p);
            count++;
        }
        return count;
    }

    // BLUE 핀에서 예약일이 지난 경우 처리:
    // - 해당 핀에 Report가 있으면 WHITE로 변경
    // - Report가 없으면 관련 Reserv/Notify 정리 후 핀 삭제
    @Transactional
    public int settleBluePinsAfterPastReservations() {
        LocalDate today = LocalDate.now();
        // 지난 예약들 수집 -> 관련 핀 집합 도출
        var pastReservs = reservRepository.findByReservDateBefore(today);
        var targetPins = pastReservs.stream()
                .map(com.example.piuda.domain.Entity.Reserv::getPin)
                .filter(p -> p.getPinColor() == Pin.PinColor.BLUE)
                .distinct()
                .collect(Collectors.toList());

        int affected = 0;
        for (Pin p : targetPins) {
            // 규칙 변경: 해당 핀의 모든 예약이 현재 날짜 기준 '지난' 경우에만 처리
            var allReservsForPin = reservRepository.findByPin(p);
            boolean hasFutureOrToday = allReservsForPin.stream()
                    .anyMatch(rv -> !rv.getReservDate().isBefore(today)); // today 또는 future 존재 시 true
            if (hasFutureOrToday) {
                // 아직 미래(또는 오늘)의 예약이 남아있으면 스킵
                continue;
            }

            List<Report> reports = reportRepository.findByPin(p);
            if (reports != null && !reports.isEmpty()) {
                // 후기(리포트) 존재 -> WHITE로 변경
                Pin updated = Pin.builder()
                        .pinId(p.getPinId())
                        .pinX(p.getPinX())
                        .pinY(p.getPinY())
                        .pinCreatedAt(p.getPinCreatedAt())
                        .region(p.getRegion())
                        .pinColor(Pin.PinColor.WHITE)
                        .build();
                pinRepository.save(updated);
                affected++;
            } else {
                // 후기 없음 -> 예약/노티 정리 후 핀 삭제
                // 1) 해당 핀의 예약 삭제 (FK 보호)
                reservRepository.deleteByPin(p);
                // 2) Notify 참조 끊기
                var notifies = notifyRepository.findByPin(p);
                if (!notifies.isEmpty()) {
                    notifies.forEach(n -> n.setPin(null));
                    notifyRepository.saveAll(notifies);
                }
                // 3) 핀 삭제
                pinRepository.delete(p);
                affected++;
            }
        }
        return affected;
    }

    // 스케줄러: 매일 새벽 3시 RED 오래된 핀 정리
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void scheduledCleanupOldRedPins() {
        cleanupOldRedPins();
    }

    // 스케줄러: 매일 새벽 3시 10분 BLUE 예약 만료 처리
    @Scheduled(cron = "0 10 3 * * *")
    @Transactional
    public void scheduledSettleBluePins() {
        settleBluePinsAfterPastReservations();
    }
}
