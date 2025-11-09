
package com.example.piuda.domain.DTO;

import com.example.piuda.domain.Entity.Pin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PinResponseDTO {
    private Long pinId;
    private Double pinX;
    private Double pinY;
    private LocalDateTime pinCreatedAt;
    private Pin.PinColor pinColor;
    private Pin.Region region;
    private String organizationName;
    // 핀에 참여한 모든 단체명 (중복 제거)
    private List<String> organizationNames;
    private Double totalTrashKg;
    private Double totalTrashL;
    // 상세용 필드 (선택): 최근 활동일, 활동 횟수, 후기 요약
    private LocalDate latestActivityDate; // 가장 최근 Report.reportDate
    private Integer activityCount;        // Report 개수
    private List<ReportSummary> reports;  // 후기 요약 리스트

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class ReportSummary {
        private Long reportId;
        private String reportTitle;
        private String reportName;
        private LocalDate reportDate;
        private Double trashKg;
        private Double trashL;
        private String reportContent;   // 후기 본문
        private List<String> photoPaths; // 후기 사진 경로 목록
    }

    public PinResponseDTO(Pin pin, String organizationName, Double totalTrashKg, Double totalTrashL) {
        this.pinId = pin.getPinId();
        this.pinX = pin.getPinX();
        this.pinY = pin.getPinY();
        this.pinCreatedAt = pin.getPinCreatedAt();
        this.pinColor = pin.getPinColor();
        this.region = pin.getRegion();
        this.organizationName = organizationName;
        this.totalTrashKg = totalTrashKg;
        this.totalTrashL = totalTrashL;
    }

    // 다중 단체명을 담는 생성자 (권장)
    public PinResponseDTO(Pin pin, List<String> organizationNames, Double totalTrashKg, Double totalTrashL) {
        this.pinId = pin.getPinId();
        this.pinX = pin.getPinX();
        this.pinY = pin.getPinY();
        this.pinCreatedAt = pin.getPinCreatedAt();
        this.pinColor = pin.getPinColor();
        this.region = pin.getRegion();
        this.organizationNames = organizationNames;
        this.totalTrashKg = totalTrashKg;
        this.totalTrashL = totalTrashL;
    }

    // 빨간 핀(RED)일 때만 포함: 제보 정보
    private List<NotifySummary> notifies;

    // 파란 핀(BLUE)일 때만 포함: 예약 정보 (현재 날짜 이후)
    private List<ReservSummary> reservations;

    // 상세 응답용 생성자 (리포트 요약, 제보 요약 포함)
    public static PinResponseDTO detailed(Pin pin,
                                          List<String> organizationNames,
                                          Double totalTrashKg,
                                          Double totalTrashL,
                                          LocalDate latestActivityDate,
                                          Integer activityCount,
                                          List<ReportSummary> reports,
                                          List<NotifySummary> notifies,
                                          List<ReservSummary> reservations) {
        PinResponseDTO dto = new PinResponseDTO(pin, organizationNames, totalTrashKg, totalTrashL);
        dto.latestActivityDate = latestActivityDate;
        dto.activityCount = activityCount;
        dto.reports = reports;
        dto.notifies = notifies;
        dto.reservations = reservations;
        return dto;
    }

    @Getter
    @AllArgsConstructor
    public static class NotifySummary {
        private Long notifyId;
        private String content;
        private List<String> photoUrls;
    }

    @Getter
    @AllArgsConstructor
    public static class ReservSummary {
        private Long reservId;
        private String reservTitle;
        private String reservOrg;
        private LocalDate reservDate;
        private Integer reservPeople;
    }
}
