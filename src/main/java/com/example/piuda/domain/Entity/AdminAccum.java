package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Admin_Accum")
public class AdminAccum {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accum_id")
    private Long accumId;

    @Column(name = "accum_org")
    private Integer accumOrg;

    @Column(name = "accum_kg")
    private Double accumKg;

    @Column(name = "accum_l")
    private Double accumL;

    @Column(name = "accum_act")
    private Integer accumAct;

    @Column(name = "accum_updated_at")
    private LocalDateTime accumUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
}