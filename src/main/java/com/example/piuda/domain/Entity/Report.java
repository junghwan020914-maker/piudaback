package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Report")
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "report_name", nullable = false, length = 20)
    private String reportName;

    @Column(name = "report_people", nullable = false)
    private Integer reportPeople;

    @Column(name = "report_title", nullable = false, length = 20)
    private String reportTitle;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "report_detail_location", length = 50)
    private String reportDetailLocation;

    @Column(name = "report_content", length = 2000)
    private String reportContent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trash_id", nullable = false, unique = true)
    private Trash trash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pin_id", nullable = false)
    private Pin pin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Org org;
}