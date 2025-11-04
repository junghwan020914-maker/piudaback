package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Private")
public class Private {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "private_id")
    private Long privateId;

    @Column(name = "private_phone", nullable = false, length = 20)
    private String privatePhone;

    @Column(name = "private_gender", nullable = false)
    private Integer privateGender;

    @Column(name = "private_photo", length = 50)
    private String privatePhoto;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}