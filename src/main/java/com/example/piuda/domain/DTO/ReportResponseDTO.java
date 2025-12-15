package com.example.piuda.domain.DTO;

import com.example.piuda.domain.Entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReportResponseDTO {
    private Long reportId;
    private String reportName;
    private Integer reportPeople;
    private String reportTitle;
    private LocalDate reportDate;
    private String reportDetailLocation;
    private String reportContent;
    private Long trashId;
    private Long pinId;
    private Double pinX;  // 경도
    private Double pinY;  // 위도
    private List<String> photoUrls;
    private String writerType;  // ANONYMOUS, USER, ORGANIZATION
    private Long writerId;      // writer의 user_id (있는 경우)
    private String writerName;  // writer의 이름 또는 단체명
    private String writerEmail; // writer의 이메일 (있는 경우)
    
    // Trash 상세 정보
    private Double trashKg;
    private Double trashL;
    private Integer trashPet;
    private Integer trashBag;
    private Integer trashNet;
    private Integer trashGlass;
    private Integer trashCan;
    private Integer trashRope;
    private Integer trashCloth;
    private Integer trashElec;
    private Integer trashEtc;

    public ReportResponseDTO(Report report, List<String> photoUrls) {
        this.reportId = report.getReportId();
        this.reportName = report.getReportName();
        this.reportPeople = report.getReportPeople();
        this.reportTitle = report.getReportTitle();
        this.reportDate = report.getReportDate();
        this.reportDetailLocation = report.getReportDetailLocation();
        this.reportContent = report.getReportContent();
        this.trashId = report.getTrash().getTrashId();
        this.pinId = report.getPin().getPinId();
        this.pinX = report.getPin().getPinX();
        this.pinY = report.getPin().getPinY();
        this.photoUrls = photoUrls;
        this.writerType = report.getWriterType().name();
        
        // Trash 정보 설정
        if (report.getTrash() != null) {
            this.trashKg = report.getTrash().getTrashKg();
            this.trashL = report.getTrash().getTrashL();
            this.trashPet = report.getTrash().getTrashPet();
            this.trashBag = report.getTrash().getTrashBag();
            this.trashNet = report.getTrash().getTrashNet();
            this.trashGlass = report.getTrash().getTrashGlass();
            this.trashCan = report.getTrash().getTrashCan();
            this.trashRope = report.getTrash().getTrashRope();
            this.trashCloth = report.getTrash().getTrashCloth();
            this.trashElec = report.getTrash().getTrashElec();
            this.trashEtc = report.getTrash().getTrashEtc();
        }
        
        // writer 정보 설정
        if (report.getWriter() != null) {
            this.writerId = report.getWriter().getUserId();
            this.writerEmail = report.getWriter().getUserEmail();
            
            // writerType에 따라 이름 설정
            if (report.getWriterType() == Report.WriterType.GROUP && report.getOrg() != null) {
                this.writerName = report.getOrg().getOrgName();
            } else {
                this.writerName = report.getWriter().getUserName();
            }
        } else {
            this.writerId = null;
            this.writerName = report.getReportName(); // 익명인 경우 reportName 사용
            this.writerEmail = null;
        }
    }

    /**
     * 메인페이지용 DTO - 최근 활동 후기 3개 조회
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MainPageDTO {
        private String orgName;
        private Long reportId;
        private Integer reportPeople;
        private String reportTitle;
        private LocalDate reportDate;
        private String reportDetailLocation;
        private Double trashKg;

        public static MainPageDTO from(Report report) {
            // org가 null인 경우 reportName 사용, 아니면 org의 orgName 사용
            String orgName = (report.getOrg() != null && report.getOrg().getOrgName() != null) 
                    ? report.getOrg().getOrgName() 
                    : report.getReportName();
            
            return MainPageDTO.builder()
                    .orgName(orgName)
                    .reportId(report.getReportId())
                    .reportPeople(report.getReportPeople())
                    .reportTitle(report.getReportTitle())
                    .reportDate(report.getReportDate())
                    .reportDetailLocation(report.getReportDetailLocation())
                    .trashKg(report.getTrash().getTrashKg())
                    .build();
        }
    }
    @Getter
    @AllArgsConstructor
    @Builder
    public static class ReportPageDTO {
        private Long reportId;
        private String reportTitle;
        private String orgName;
        private LocalDate reportDate;
        private String reportDetailLocation;
        public static ReportPageDTO from(Report report) {
            String orgName = (report.getOrg() != null && report.getOrg().getOrgName() != null)
                    ? report.getOrg().getOrgName()
                    : report.getReportName();
            return ReportPageDTO.builder()
                    .reportId(report.getReportId())
                    .reportTitle(report.getReportTitle())
                    .orgName(orgName)
                    .reportDate(report.getReportDate())
                    .reportDetailLocation(report.getReportDetailLocation())
                    .build();
        }
    }
}
