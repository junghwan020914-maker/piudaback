package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Pin")
public class Pin {

    public enum Region {
        WEST_SEA, EAST_SEA, SOUTH_SEA, JEJU, ULLEUNG
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pin_id")
    private Long pinId;

    @Column(name = "pin_x", nullable = false)
    private Double pinX;

    @Column(name = "pin_y", nullable = false)
    private Double pinY;

    @Column(name = "pin_created_at", nullable = false)
    private LocalDateTime pinCreatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "pin_color", nullable = false)
    @Builder.Default
    private PinColor pinColor = PinColor.WHITE;

    public enum PinColor {
        WHITE, BLUE, RED
    }
}