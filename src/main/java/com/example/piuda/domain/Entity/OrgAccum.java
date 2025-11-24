package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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

    @Column(name = "trash_pet")
    private Integer accumtrashPet;

    @Column(name = "trash_bag")
    private Integer accumtrashBag;

    @Column(name = "trash_net")
    private Integer accumtrashNet;

    @Column(name = "trash_glass")
    private Integer accumtrashGlass;

    @Column(name = "trash_can")
    private Integer accumtrashCan;

    @Column(name = "trash_rope")
    private Integer accumtrashRope;

    @Column(name = "trash_cloth")
    private Integer accumtrashCloth;

    @Column(name = "trash_elec")
    private Integer accumtrashElec;

    @Column(name = "trash_etc")
    private Integer accumtrashEtc;

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;
}