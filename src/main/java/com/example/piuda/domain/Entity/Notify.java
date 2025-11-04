package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Notify")
public class Notify {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notify_id")
    private Long notifyId;

    @Column(name = "notify_x", nullable = false)
    private Double notifyX;

    @Column(name = "notify_y", nullable = false)
    private Double notifyY;

    @Column(name = "notify_created_at", nullable = false)
    private LocalDateTime notifyCreatedAt;

    @Column(name = "notify_content", length = 2000)
    private String notifyContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "notify_status", nullable = false)
    @Builder.Default
    private NotifyStatus notifyStatus = NotifyStatus.WAIT;

    @ManyToOne
    @JoinColumn(name = "pin_id")
    private Pin pin;

    public enum NotifyStatus {
        WAIT, ACCEPT, REJECT
    }
}