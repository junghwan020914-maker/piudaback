package com.example.piuda.config;

import com.example.piuda.Notify.NotifyRepository;
import com.example.piuda.Pin.PinRepository;
import com.example.piuda.Report.ReportRepository;
import com.example.piuda.Reserv.ReservRepository;
import com.example.piuda.Trash.TrashRepository;
import com.example.piuda.domain.Entity.Notify;
import com.example.piuda.domain.Entity.Pin;
import com.example.piuda.domain.Entity.Report;
import com.example.piuda.domain.Entity.Reserv;
import com.example.piuda.domain.Entity.Trash;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PinRepository pinRepository;
    private final ReportRepository reportRepository;
    private final ReservRepository reservRepository;
    private final NotifyRepository notifyRepository;
    private final TrashRepository trashRepository;

    @Override
    public void run(ApplicationArguments args) {
        // 이미 데이터가 있으면 시드 작업 생략
        if (pinRepository.count() > 0) {
            return;
        }

        // 1) 기본 핀 생성
        Pin pinA = pinRepository.save(Pin.builder()
                .pinX(126.9784)
                .pinY(37.5665)
                .pinCreatedAt(LocalDateTime.now().minusDays(30))
                .region(Pin.Region.WEST_SEA)
                .pinColor(Pin.PinColor.WHITE)
                .build());

        Pin pinBluePastWithReview = pinRepository.save(Pin.builder()
                .pinX(129.0756)
                .pinY(35.1796)
                .pinCreatedAt(LocalDateTime.now().minusDays(20))
                .region(Pin.Region.EAST_SEA)
                .pinColor(Pin.PinColor.BLUE)
                .build());

        Pin pinBluePastNoReview = pinRepository.save(Pin.builder()
                .pinX(126.5312)
                .pinY(33.4996)
                .pinCreatedAt(LocalDateTime.now().minusDays(15))
                .region(Pin.Region.JEJU)
                .pinColor(Pin.PinColor.BLUE)
                .build());

        Pin pinBlueFuture = pinRepository.save(Pin.builder()
                .pinX(128.6014)
                .pinY(35.8714)
                .pinCreatedAt(LocalDateTime.now().minusDays(5))
                .region(Pin.Region.SOUTH_SEA)
                .pinColor(Pin.PinColor.BLUE)
                .build());

        Pin pinRedOld = pinRepository.save(Pin.builder()
                .pinX(127.0)
                .pinY(36.0)
                .pinCreatedAt(LocalDateTime.now().minusDays(25))
                .region(Pin.Region.ULLEUNG)
                .pinColor(Pin.PinColor.RED)
                .build());

        // 2) Report(후기) + Trash 생성
        Trash t1 = trashRepository.save(Trash.builder()
                .trashKg(12.5)
                .trashL(8.0)
                .trashPet(10)
                .trashBag(2)
                .build());
        reportRepository.save(Report.builder()
                .reportName("OrgA")
                .reportPeople(5)
                .reportTitle("서해 정화 활동")
                .reportDate(LocalDate.now().minusDays(10))
                .trash(t1)
                .pin(pinA)
                .build());

        Trash t2 = trashRepository.save(Trash.builder()
                .trashKg(7.0)
                .trashL(3.5)
                .trashCan(5)
                .trashGlass(3)
                .build());
        reportRepository.save(Report.builder()
                .reportName("OrgB")
                .reportPeople(3)
                .reportTitle("서해 해변 클린업")
                .reportDate(LocalDate.now().minusDays(8))
                .trash(t2)
                .pin(pinA)
                .build());

        Trash t3 = trashRepository.save(Trash.builder()
                .trashKg(4.2)
                .trashL(2.0)
                .trashNet(1)
                .trashEtc(2)
                .build());
        reportRepository.save(Report.builder()
                .reportName("OrgA")
                .reportPeople(4)
                .reportTitle("동해 정화")
                .reportDate(LocalDate.now().minusDays(5))
                .trash(t3)
                .pin(pinBluePastWithReview)
                .build());

        // 3) Reserv 생성 (지난/미래)
        reservRepository.save(Reserv.builder()
                .reservTitle("지난 예약 - 후기 있음")
                .reservOrg("OrgA")
                .reservPeople(10)
                .reservX(pinBluePastWithReview.getPinX())
                .reservY(pinBluePastWithReview.getPinY())
                .reservCreatedAt(LocalDateTime.now().minusDays(18))
                .reservDate(LocalDate.now().minusMonths(9))
                .pin(pinBluePastWithReview)
                .build());

        reservRepository.save(Reserv.builder()
                .reservTitle("지난 예약 - 후기 없음")
                .reservOrg("OrgC")
                .reservPeople(6)
                .reservX(pinBluePastNoReview.getPinX())
                .reservY(pinBluePastNoReview.getPinY())
                .reservCreatedAt(LocalDateTime.now().minusDays(14))
                .reservDate(LocalDate.now().minusMonths(10))
                .pin(pinBluePastNoReview)
                .build());

        reservRepository.save(Reserv.builder()
                .reservTitle("미래 예약")
                .reservOrg("OrgD")
                .reservPeople(12)
                .reservX(pinBlueFuture.getPinX())
                .reservY(pinBlueFuture.getPinY())
                .reservCreatedAt(LocalDateTime.now().minusDays(3))
                .reservDate(LocalDate.now().plusMonths(1))
                .pin(pinBlueFuture)
                .build());

        // 4) Notify 생성 (WAIT/ACCEPT)
        notifyRepository.save(Notify.builder()
                .notifyX(126.99)
                .notifyY(37.57)
                .notifyCreatedAt(LocalDateTime.now().minusDays(1))
                .notifyContent("쓰레기 제보 - 확인 요청")
                .notifyStatus(Notify.NotifyStatus.WAIT)
                .build());

        notifyRepository.save(Notify.builder()
                .notifyX(pinRedOld.getPinX())
                .notifyY(pinRedOld.getPinY())
                .notifyCreatedAt(LocalDateTime.now().minusDays(16))
                .notifyContent("오래된 빨간 핀 인근 제보")
                .notifyStatus(Notify.NotifyStatus.ACCEPT)
                .pin(pinRedOld)
                .build());
    }
}
