package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Report_Photo")
public class ReportPhoto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rphoto_id")
    private Long rphotoId;

    @Column(name = "rphoto_path", length = 1024)
    private String rphotoPath;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
}