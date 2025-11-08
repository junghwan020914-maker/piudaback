package com.example.piuda.Pin;

import com.example.piuda.Report.ReportRepository;
import com.example.piuda.Notify.NotifyRepository;
import com.example.piuda.Reserv.ReservRepository;
import com.example.piuda.ReportPhoto.ReportPhotoRepository;
import com.example.piuda.domain.Entity.DTO.PinResponseDTO;
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

    public List<Pin> getAllPins() {
        return pinRepository.findAll();
    }

    // 초기 지도 로드용: 전체 핀 데이터(클라이언트 필터링용 요약 포함) 반환
    public List<PinResponseDTO> getAllPinsForClient() {
    List<Pin> pins = pinRepository.findAll();
    return pins.stream().map(pin -> {
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

        // 초기 로드에서는 사진 경로 조회 비용을 줄이기 위해 사진은 생략하고 빈 리스트 전달
        List<PinResponseDTO.ReportSummary> summaries = reports.stream()
            .map(r -> new PinResponseDTO.ReportSummary(
                r.getReportId(),
                r.getReportTitle(),
                r.getReportName(),
                r.getReportDate(),
                r.getTrash() != null ? r.getTrash().getTrashKg() : null,
                r.getTrash() != null ? r.getTrash().getTrashL() : null,
                r.getReportContent(),
                java.util.List.of()
            ))
            .collect(Collectors.toList());

        return PinResponseDTO.detailed(
            pin,
            orgNames,
            totalKg,
            totalL,
            latestActivityDate,
            activityCount,
            summaries
        );
    }).collect(Collectors.toList());
    }

    // 핀 상세 조회 (PinResponseDTO 재사용)
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

    return PinResponseDTO.detailed(
        pin,
        orgNames,
        totalKg,
        totalL,
        latestActivityDate,
        activityCount,
        summaries
    );
    }

// 필터링된 핀 반환 메소드
    public List<PinResponseDTO> getFilteredPins(
            LocalDate startDate,
            LocalDate endDate,
            List<String> organizationNames,
            Pin.Region region,
        Double minKg,
        Double minL,
        Double maxKg,
        Double maxL) {

        // 빈 리스트는 필터 미적용을 위해 null로 전달
        List<String> orgNamesParam = (organizationNames == null || organizationNames.isEmpty()) ? null : organizationNames;

    List<Pin> pins = pinRepository.findWithFilters(startDate, endDate, orgNamesParam, region, minKg, minL, maxKg, maxL);

        return pins.stream().map(pin -> {
            List<Report> reports = reportRepository.findByPin(pin);
            List<String> orgNames = reports.stream()
                    .map(Report::getReportName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            Double totalKg = reports.stream().map(Report::getTrash).mapToDouble(Trash::getTrashKg).sum();
            Double totalL = reports.stream().map(Report::getTrash).mapToDouble(Trash::getTrashL).sum();
            return new PinResponseDTO(pin, orgNames, totalKg, totalL);
        }).collect(Collectors.toList());
    }

    // 일정 거리 내 중복 체크 (예: 50m)
    private boolean isPinNearby(double x, double y, double distanceMeters) {
        List<Pin> pins = pinRepository.findAll();
        for (Pin pin : pins) {
            double dx = pin.getPinX() - x;
            double dy = pin.getPinY() - y;
            double dist = Math.sqrt(dx * dx + dy * dy) * 111_000; // 위도/경도 -> m 변환(간략)
            if (dist < distanceMeters) return true;
        }
        return false;
    }


    
        // 제보 상태가 수락으로 변경된경우 핀추가, 해당 위치의 핀 이미 존재시 존재하고 있는 핀 반환(red 핀 생성)
        public Optional<Pin> addPinFromNotifyAndGet(double x, double y, double distanceMeters) {
            // 근처에 핀이 없으면 새로 생성 후 반환
            if (!isPinNearby(x, y, distanceMeters)) {
                Pin pin = Pin.builder()
                        .pinX(x)
                        .pinY(y)
                        .pinCreatedAt(java.time.LocalDateTime.now())
                        .pinColor(Pin.PinColor.RED)
                        .build();
                Pin saved = pinRepository.save(pin);
                return Optional.of(saved);
            }
            // 근처에 핀이 있으면 그 핀을 찾아 반환
            List<Pin> pins = pinRepository.findAll();
            for (Pin p : pins) {
                double dx = p.getPinX() - x;
                double dy = p.getPinY() - y;
                double dist = Math.sqrt(dx * dx + dy * dy) * 111_000;
                if (dist < distanceMeters) {
                    return Optional.of(p);
                }
            }
            return Optional.empty();
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
