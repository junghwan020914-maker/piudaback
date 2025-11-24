package com.example.piuda.domain.DTO;

import com.example.piuda.domain.Entity.OrgAccum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardResponseDTO {

    /**
     * 단체 대시보드 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrgDashboardDTO {
        // 단체 기본 정보
        private Long orgId;
        private String orgName;
        
        // 누적 데이터 (OrgAccum)
        private Integer accumPeople;      // 누적 인원
        private Double accumKg;           // 누적 kg
        private Double accumL;            // 누적 L
        private Integer accumAct;         // 누적 활동 수
        private LocalDateTime accumUpdatedAt; // 마지막 업데이트 시간
        
        // 쓰레기 세부 항목
        private Integer accumTrashPet;    // 페트병
        private Integer accumTrashBag;    // 비닐봉지
        private Integer accumTrashNet;    // 그물
        private Integer accumTrashGlass;  // 유리
        private Integer accumTrashCan;    // 캔
        private Integer accumTrashRope;   // 로프
        private Integer accumTrashCloth;  // 천/의류
        private Integer accumTrashElec;   // 전자제품
        private Integer accumTrashEtc;    // 기타
        
        // 해당 단체가 작성한 후기 목록
        private List<ReportResponseDTO> reports;
        
        /**
         * OrgAccum 엔티티로부터 DTO 생성
         */
        public static OrgDashboardDTO from(OrgAccum orgAccum, List<ReportResponseDTO> reports) {
            return OrgDashboardDTO.builder()
                    .orgId(orgAccum.getOrg().getOrgId())
                    .orgName(orgAccum.getOrg().getOrgName())
                    .accumPeople(orgAccum.getAccumPeople())
                    .accumKg(orgAccum.getAccumKg())
                    .accumL(orgAccum.getAccumL())
                    .accumAct(orgAccum.getAccumAct())
                    .accumUpdatedAt(orgAccum.getAccumUpdatedAt())
                    .accumTrashPet(orgAccum.getAccumtrashPet())
                    .accumTrashBag(orgAccum.getAccumtrashBag())
                    .accumTrashNet(orgAccum.getAccumtrashNet())
                    .accumTrashGlass(orgAccum.getAccumtrashGlass())
                    .accumTrashCan(orgAccum.getAccumtrashCan())
                    .accumTrashRope(orgAccum.getAccumtrashRope())
                    .accumTrashCloth(orgAccum.getAccumtrashCloth())
                    .accumTrashElec(orgAccum.getAccumtrashElec())
                    .accumTrashEtc(orgAccum.getAccumtrashEtc())
                    .reports(reports)
                    .build();
        }
    }

    /**
     * 관리자 대시보드 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdminDashboardDTO {
        // 관리자 기본 정보
        private Long adminId;
        
        // 누적 데이터 (AdminAccum)
        private Integer accumOrg;         // 누적 단체 수
        private Double accumKg;           // 누적 kg
        private Double accumL;            // 누적 L
        private Integer accumAct;         // 누적 활동 수
        private LocalDateTime accumUpdatedAt; // 마지막 업데이트 시간
        
        // 쓰레기 세부 항목
        private Integer accumTrashPet;    // 페트병
        private Integer accumTrashBag;    // 비닐봉지
        private Integer accumTrashNet;    // 그물
        private Integer accumTrashGlass;  // 유리
        private Integer accumTrashCan;    // 캠
        private Integer accumTrashRope;   // 로프
        private Integer accumTrashCloth;  // 천/의류
        private Integer accumTrashElec;   // 전자제품
        private Integer accumTrashEtc;    // 기타
        
        // 제보 목록
        private List<NotifyDTO> notifies;
        
        /**
         * AdminAccum 엔티티로부터 DTO 생성
         */
        public static AdminDashboardDTO from(com.example.piuda.domain.Entity.AdminAccum adminAccum, List<NotifyDTO> notifies) {
            return AdminDashboardDTO.builder()
                    .adminId(adminAccum.getAdmin().getAdminId())
                    .accumOrg(adminAccum.getAccumOrg())
                    .accumKg(adminAccum.getAccumKg())
                    .accumL(adminAccum.getAccumL())
                    .accumAct(adminAccum.getAccumAct())
                    .accumUpdatedAt(adminAccum.getAccumUpdatedAt())
                    .accumTrashPet(adminAccum.getAccumtrashPet())
                    .accumTrashBag(adminAccum.getAccumtrashBag())
                    .accumTrashNet(adminAccum.getAccumtrashNet())
                    .accumTrashGlass(adminAccum.getAccumtrashGlass())
                    .accumTrashCan(adminAccum.getAccumtrashCan())
                    .accumTrashRope(adminAccum.getAccumtrashRope())
                    .accumTrashCloth(adminAccum.getAccumtrashCloth())
                    .accumTrashElec(adminAccum.getAccumtrashElec())
                    .accumTrashEtc(adminAccum.getAccumtrashEtc())
                    .notifies(notifies)
                    .build();
        }
    }

    /**
     * 제보 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotifyDTO {
        private Long notifyId;
        private Double notifyX;
        private Double notifyY;
        private LocalDateTime notifyCreatedAt;
        private String notifyContent;
        private String notifyStatus;  // WAIT, ACCEPT, REJECT
        private Long pinId;
        private List<String> photoUrls;
        
        public static NotifyDTO from(com.example.piuda.domain.Entity.Notify notify, List<String> photoUrls) {
            return NotifyDTO.builder()
                    .notifyId(notify.getNotifyId())
                    .notifyX(notify.getNotifyX())
                    .notifyY(notify.getNotifyY())
                    .notifyCreatedAt(notify.getNotifyCreatedAt())
                    .notifyContent(notify.getNotifyContent())
                    .notifyStatus(notify.getNotifyStatus().name())
                    .pinId(notify.getPin() != null ? notify.getPin().getPinId() : null)
                    .photoUrls(photoUrls)
                    .build();
        }
    }
}
