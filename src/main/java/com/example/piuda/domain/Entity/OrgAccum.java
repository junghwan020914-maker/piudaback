package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Org_Accum")
public class OrgAccum {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accum_id")
    private Long accumId;

    @Column(name = "accum_people")
    private Integer accumPeople;

    @Column(name = "accum_kg")
    private Double accumKg;

    @Column(name = "accum_l")
    private Double accumL;

    @Column(name = "accum_act")
    private Integer accumAct;

    @Column(name = "accum_updated_at")
    private LocalDateTime accumUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;
}