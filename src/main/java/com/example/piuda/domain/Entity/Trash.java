package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Trash")
public class Trash {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trash_id")
    private Long trashId;

    @Column(name = "trash_kg", nullable = false)
    private Double trashKg;

    @Column(name = "trash_l", nullable = false)
    private Double trashL;

    @Column(name = "trash_pet")
    private Integer trashPet;

    @Column(name = "trash_bag")
    private Integer trashBag;

    @Column(name = "trash_net")
    private Integer trashNet;

    @Column(name = "trash_glass")
    private Integer trashGlass;

    @Column(name = "trash_can")
    private Integer trashCan;

    @Column(name = "trash_rope")
    private Integer trashRope;

    @Column(name = "trash_cloth")
    private Integer trashCloth;

    @Column(name = "trash_elec")
    private Integer trashElec;

    @Column(name = "trash_etc")
    private Integer trashEtc;
}