package com.example.piuda.domain.DTO;

import com.example.piuda.domain.Entity.Report;
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
    private List<String> photoUrls;

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
        this.photoUrls = photoUrls;
    }
}
