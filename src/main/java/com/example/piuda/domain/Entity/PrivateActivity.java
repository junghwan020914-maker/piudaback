package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Private_Activity", uniqueConstraints = {
        @UniqueConstraint(name = "uq_private_report", columnNames = {"private_id", "report_id"})
})
public class PrivateActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "private_activity_Id")
    private Long PrivateActivityId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime CreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "private_id", nullable = false)
    private Private privateUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
}
