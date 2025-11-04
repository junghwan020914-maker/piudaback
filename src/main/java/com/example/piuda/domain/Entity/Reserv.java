package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Reserv")
public class Reserv {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserv_id")
    private Long reservId;

    @Column(name = "reserv_title", nullable = false, length = 50)
    private String reservTitle;

    @Column(name = "reserv_org", nullable = false, length = 50)
    private String reservOrg;

    @Column(name = "reserv_people", nullable = false)
    private Integer reservPeople;

    @Column(name = "reserv_x", nullable = false)
    private Double reservX;

    @Column(name = "reserv_y", nullable = false)
    private Double reservY;

    @Column(name = "reserv_created_at", nullable = false)
    private LocalDateTime reservCreatedAt;

    @Column(name = "reserv_date", nullable = false)
    private LocalDate reservDate;

    @ManyToOne
    @JoinColumn(name = "pin_id", nullable = false)
    private Pin pin;
}