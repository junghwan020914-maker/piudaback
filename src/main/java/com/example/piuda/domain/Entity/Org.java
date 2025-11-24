package com.example.piuda.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "Org")
public class Org {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "org_introduce", length = 1000)
    private String orgIntroduce;

    @Column(name = "org_location", length = 50)
    private String orgLocation;

    @Column(name = "org_logo", length = 50)
    private String orgLogo;

    @Column(name = "org_insta", length = 30)
    private String orgInsta;

    @Column(name = "org_site", length = 50)
    private String orgSite;

    @Column(name = "org_name", length = 20)
    private String orgName;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}