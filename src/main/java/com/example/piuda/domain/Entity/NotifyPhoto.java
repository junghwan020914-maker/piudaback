package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Notify_Photo")
public class NotifyPhoto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nphoto_id")
    private Long nphotoId;

    @Column(name = "nphoto_path", nullable = false, length = 1024)
    private String nphotoPath;

    @ManyToOne
    @JoinColumn(name = "notify_id", nullable = false)
    private Notify notify;
}