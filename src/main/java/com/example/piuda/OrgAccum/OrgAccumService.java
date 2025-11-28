package com.example.piuda.OrgAccum;

import com.example.piuda.Org.OrgRepository;
import com.example.piuda.Report.ReportRepository;
import com.example.piuda.domain.Entity.Org;
import com.example.piuda.domain.Entity.OrgAccum;
import com.example.piuda.domain.Entity.Report;
import com.example.piuda.domain.Entity.Trash;
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
public class OrgAccumService {

    private final OrgRepository orgRepository;
    private final OrgAccumRepository orgAccumRepository;
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
     * 단체 누적 데이터 증분 업데이트 (후기 작성 시)
     * 기존 누적 값에 새로운 값을 더하는 방식
     */
    @Transactional
    public void incrementOrgAccumulation(Org org, Report report) {
        log.info("단체 누적 데이터 증분 업데이트 시작: {}", org.getOrgName());
        
        try {
            OrgAccum orgAccum = orgAccumRepository.findByOrg(org)
                    .orElseGet(() -> {
                        OrgAccum newAccum = OrgAccum.builder()
                                .org(org)
                                .accumPeople(0)
                                .accumKg(0.0)
                                .accumL(0.0)
                                .accumAct(0)
                                .accumtrashPet(0)
                                .accumtrashBag(0)
                                .accumtrashNet(0)
                                .accumtrashGlass(0)
                                .accumtrashCan(0)
                                .accumtrashRope(0)
                                .accumtrashCloth(0)
                                .accumtrashElec(0)
                                .accumtrashEtc(0)
                                .build();
                        return orgAccumRepository.save(newAccum);
                    });

            Trash trash = report.getTrash();

            // 기존 값에 더하기
            orgAccum.setAccumPeople(orgAccum.getAccumPeople() + report.getReportPeople());
            orgAccum.setAccumKg(orgAccum.getAccumKg() + (trash.getTrashKg() != null ? trash.getTrashKg() : 0.0));
            orgAccum.setAccumL(orgAccum.getAccumL() + (trash.getTrashL() != null ? trash.getTrashL() : 0.0));
            orgAccum.setAccumAct(orgAccum.getAccumAct() + 1);
            orgAccum.setAccumtrashPet(orgAccum.getAccumtrashPet() + (trash.getTrashPet() != null ? trash.getTrashPet() : 0));
            orgAccum.setAccumtrashBag(orgAccum.getAccumtrashBag() + (trash.getTrashBag() != null ? trash.getTrashBag() : 0));
            orgAccum.setAccumtrashNet(orgAccum.getAccumtrashNet() + (trash.getTrashNet() != null ? trash.getTrashNet() : 0));
            orgAccum.setAccumtrashGlass(orgAccum.getAccumtrashGlass() + (trash.getTrashGlass() != null ? trash.getTrashGlass() : 0));
            orgAccum.setAccumtrashCan(orgAccum.getAccumtrashCan() + (trash.getTrashCan() != null ? trash.getTrashCan() : 0));
            orgAccum.setAccumtrashRope(orgAccum.getAccumtrashRope() + (trash.getTrashRope() != null ? trash.getTrashRope() : 0));
            orgAccum.setAccumtrashCloth(orgAccum.getAccumtrashCloth() + (trash.getTrashCloth() != null ? trash.getTrashCloth() : 0));
            orgAccum.setAccumtrashElec(orgAccum.getAccumtrashElec() + (trash.getTrashElec() != null ? trash.getTrashElec() : 0));
            orgAccum.setAccumtrashEtc(orgAccum.getAccumtrashEtc() + (trash.getTrashEtc() != null ? trash.getTrashEtc() : 0));
            orgAccum.setAccumUpdatedAt(LocalDateTime.now());

            orgAccumRepository.save(orgAccum);
            log.info("단체 누적 데이터 증분 업데이트 완료: {} (활동: {}회, 인원: {}명, Kg: {}, L: {})",
                    org.getOrgName(), orgAccum.getAccumAct(), orgAccum.getAccumPeople(),
                    orgAccum.getAccumKg(), orgAccum.getAccumL());
        } catch (Exception e) {
            log.error("단체 ID: {} 누적 데이터 증분 업데이트 중 오류 발생", org.getOrgId(), e);
        }
    }
}
