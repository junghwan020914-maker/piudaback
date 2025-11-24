package com.example.piuda.Dashboard;

import com.example.piuda.Admin.AdminRepository;
import com.example.piuda.AdminAccum.AdminAccumRepository;
import com.example.piuda.Org.OrgRepository;
import com.example.piuda.OrgAccum.OrgAccumRepository;
import com.example.piuda.Report.ReportRepository;
import com.example.piuda.domain.Entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccumulationService {

    private final OrgRepository orgRepository;
    private final OrgAccumRepository orgAccumRepository;
    private final AdminRepository adminRepository;
    private final AdminAccumRepository adminAccumRepository;
    private final ReportRepository reportRepository;

    /**
     * 매일 새벽 3시에 단체별 누적 데이터를 업데이트
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void updateOrgAccumulation() {
        log.info("단체 누적 데이터 업데이트 시작");
        
        List<Org> allOrgs = orgRepository.findAll();
        
        for (Org org : allOrgs) {
            try {
                // 해당 단체의 누적 데이터 조회 또는 생성
                OrgAccum orgAccum = orgAccumRepository.findByOrg(org)
                        .orElseGet(() -> OrgAccum.builder()
                                .org(org)
                                .accumPeople(0)
                                .accumKg(0.0)
                                .accumL(0.0)
                                .accumAct(0)
                                .build());
                
                // Report 테이블에서 해당 org_id를 참조하는 후기들의 누적값 계산
                Integer totalPeople = reportRepository.sumPeopleByOrg(org);
                Double totalKg = reportRepository.sumKgByOrg(org);
                Double totalL = reportRepository.sumLByOrg(org);
                Integer totalAct = reportRepository.countByOrg(org);
                
                // 쓰레기 세부 항목 집계
                Integer totalPet = reportRepository.sumTrashPetByOrg(org);
                Integer totalBag = reportRepository.sumTrashBagByOrg(org);
                Integer totalNet = reportRepository.sumTrashNetByOrg(org);
                Integer totalGlass = reportRepository.sumTrashGlassByOrg(org);
                Integer totalCan = reportRepository.sumTrashCanByOrg(org);
                Integer totalRope = reportRepository.sumTrashRopeByOrg(org);
                Integer totalCloth = reportRepository.sumTrashClothByOrg(org);
                Integer totalElec = reportRepository.sumTrashElecByOrg(org);
                Integer totalEtc = reportRepository.sumTrashEtcByOrg(org);
                
                // 값이 null인 경우 0으로 처리
                orgAccum.setAccumPeople(totalPeople != null ? totalPeople : 0);
                orgAccum.setAccumKg(totalKg != null ? totalKg : 0.0);
                orgAccum.setAccumL(totalL != null ? totalL : 0.0);
                orgAccum.setAccumAct(totalAct != null ? totalAct : 0);
                orgAccum.setAccumtrashPet(totalPet);
                orgAccum.setAccumtrashBag(totalBag);
                orgAccum.setAccumtrashNet(totalNet);
                orgAccum.setAccumtrashGlass(totalGlass);
                orgAccum.setAccumtrashCan(totalCan);
                orgAccum.setAccumtrashRope(totalRope);
                orgAccum.setAccumtrashCloth(totalCloth);
                orgAccum.setAccumtrashElec(totalElec);
                orgAccum.setAccumtrashEtc(totalEtc);
                orgAccum.setAccumUpdatedAt(LocalDateTime.now());
                
                orgAccumRepository.save(orgAccum);
                
                log.info("단체 ID: {} 누적 데이터 업데이트 완료 - 인원: {}, KG: {}, L: {}, 활동수: {}", 
                        org.getOrgId(), orgAccum.getAccumPeople(), orgAccum.getAccumKg(), 
                        orgAccum.getAccumL(), orgAccum.getAccumAct());
                
            } catch (Exception e) {
                log.error("단체 ID: {} 누적 데이터 업데이트 중 오류 발생", org.getOrgId(), e);
            }
        }
        
        log.info("단체 누적 데이터 업데이트 완료");
    }

    /**
     * 매일 새벽 3시에 관리자별 누적 데이터를 업데이트
     * 관리자는 관리자 ID와 상관없이 모든 후기를 누적
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void updateAdminAccumulation() {
        log.info("관리자 누적 데이터 업데이트 시작");
        
        List<Admin> allAdmins = adminRepository.findAll();
        
        // 전체 후기에서 누적값 계산 (관리자는 모든 후기를 누적)
        Double totalKg = reportRepository.sumTotalKg();
        Double totalL = reportRepository.sumTotalL();
        Integer totalAct = reportRepository.countTotal();
        Integer totalOrg = reportRepository.countDistinctOrgs();
        
        // 전체 쓰레기 세부 항목 집계
        Integer totalPet = reportRepository.sumTotalTrashPet();
        Integer totalBag = reportRepository.sumTotalTrashBag();
        Integer totalNet = reportRepository.sumTotalTrashNet();
        Integer totalGlass = reportRepository.sumTotalTrashGlass();
        Integer totalCan = reportRepository.sumTotalTrashCan();
        Integer totalRope = reportRepository.sumTotalTrashRope();
        Integer totalCloth = reportRepository.sumTotalTrashCloth();
        Integer totalElec = reportRepository.sumTotalTrashElec();
        Integer totalEtc = reportRepository.sumTotalTrashEtc();
        
        for (Admin admin : allAdmins) {
            try {
                // 해당 관리자의 누적 데이터 조회 또는 생성
                AdminAccum adminAccum = adminAccumRepository.findByAdmin(admin)
                        .orElseGet(() -> AdminAccum.builder()
                                .admin(admin)
                                .accumOrg(0)
                                .accumKg(0.0)
                                .accumL(0.0)
                                .accumAct(0)
                                .build());
                
                // 모든 관리자에게 동일한 전체 누적값 적용
                adminAccum.setAccumOrg(totalOrg != null ? totalOrg : 0);
                adminAccum.setAccumKg(totalKg != null ? totalKg : 0.0);
                adminAccum.setAccumL(totalL != null ? totalL : 0.0);
                adminAccum.setAccumAct(totalAct != null ? totalAct : 0);
                adminAccum.setAccumtrashPet(totalPet);
                adminAccum.setAccumtrashBag(totalBag);
                adminAccum.setAccumtrashNet(totalNet);
                adminAccum.setAccumtrashGlass(totalGlass);
                adminAccum.setAccumtrashCan(totalCan);
                adminAccum.setAccumtrashRope(totalRope);
                adminAccum.setAccumtrashCloth(totalCloth);
                adminAccum.setAccumtrashElec(totalElec);
                adminAccum.setAccumtrashEtc(totalEtc);
                adminAccum.setAccumUpdatedAt(LocalDateTime.now());
                
                adminAccumRepository.save(adminAccum);
                
                log.info("관리자 ID: {} 누적 데이터 업데이트 완료 - 단체수: {}, KG: {}, L: {}, 활동수: {}", 
                        admin.getAdminId(), adminAccum.getAccumOrg(), adminAccum.getAccumKg(), 
                        adminAccum.getAccumL(), adminAccum.getAccumAct());
                
            } catch (Exception e) {
                log.error("관리자 ID: {} 누적 데이터 업데이트 중 오류 발생", admin.getAdminId(), e);
            }
        }
        
        log.info("관리자 누적 데이터 업데이트 완료");
    }
}
