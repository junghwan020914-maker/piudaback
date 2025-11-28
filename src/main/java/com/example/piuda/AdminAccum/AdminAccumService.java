package com.example.piuda.AdminAccum;

import com.example.piuda.Admin.AdminRepository;
import com.example.piuda.Notify.NotifyRepository;
import com.example.piuda.Report.ReportRepository;
import com.example.piuda.User.UserRepository;
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
public class AdminAccumService {

    private final AdminRepository adminRepository;
    private final AdminAccumRepository adminAccumRepository;
    private final ReportRepository reportRepository;
    private final NotifyRepository notifyRepository;
    private final UserRepository userRepository;

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
        Integer totalNotify = (int) notifyRepository.count();
        Integer totalUser = (int) userRepository.count();
        
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
                                .accumNotify(0)
                                .accumUser(0)
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
                adminAccum.setAccumNotify(totalNotify != null ? totalNotify : 0);
                adminAccum.setAccumUser(totalUser != null ? totalUser : 0);
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

    /**
     * 관리자 누적 데이터 증분 업데이트 (후기 작성 시)
     * 기존 누적 값에 새로운 값을 더하는 방식
     */
    @Transactional
    public void incrementAdminAccumulation(Report report) {
        log.info("관리자 누적 데이터 증분 업데이트 시작");

        List<Admin> admins = adminRepository.findAll();
        Trash trash = report.getTrash();
        Org org = report.getOrg();

        for (Admin admin : admins) {
            try {
                AdminAccum adminAccum = adminAccumRepository.findByAdmin(admin)
                        .orElseGet(() -> {
                            AdminAccum newAccum = AdminAccum.builder()
                                    .admin(admin)
                                    .accumKg(0.0)
                                    .accumL(0.0)
                                    .accumAct(0)
                                    .accumOrg(0)
                                    .accumNotify(0)
                                    .accumUser(0)
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
                            return adminAccumRepository.save(newAccum);
                        });

                // 기존 값에 더하기
                adminAccum.setAccumKg(adminAccum.getAccumKg() + (trash.getTrashKg() != null ? trash.getTrashKg() : 0.0));
                adminAccum.setAccumL(adminAccum.getAccumL() + (trash.getTrashL() != null ? trash.getTrashL() : 0.0));
                adminAccum.setAccumAct(adminAccum.getAccumAct() + 1);
                
                // 단체 후기인 경우 단체 수도 증가 (첫 활동인 경우만)
                if (org != null) {
                    Integer orgActivityCount = reportRepository.countByOrg(org);
                    if (orgActivityCount != null && orgActivityCount == 1) {
                        adminAccum.setAccumOrg(adminAccum.getAccumOrg() + 1);
                    }
                }
                
                adminAccum.setAccumtrashPet(adminAccum.getAccumtrashPet() + (trash.getTrashPet() != null ? trash.getTrashPet() : 0));
                adminAccum.setAccumtrashBag(adminAccum.getAccumtrashBag() + (trash.getTrashBag() != null ? trash.getTrashBag() : 0));
                adminAccum.setAccumtrashNet(adminAccum.getAccumtrashNet() + (trash.getTrashNet() != null ? trash.getTrashNet() : 0));
                adminAccum.setAccumtrashGlass(adminAccum.getAccumtrashGlass() + (trash.getTrashGlass() != null ? trash.getTrashGlass() : 0));
                adminAccum.setAccumtrashCan(adminAccum.getAccumtrashCan() + (trash.getTrashCan() != null ? trash.getTrashCan() : 0));
                adminAccum.setAccumtrashRope(adminAccum.getAccumtrashRope() + (trash.getTrashRope() != null ? trash.getTrashRope() : 0));
                adminAccum.setAccumtrashCloth(adminAccum.getAccumtrashCloth() + (trash.getTrashCloth() != null ? trash.getTrashCloth() : 0));
                adminAccum.setAccumtrashElec(adminAccum.getAccumtrashElec() + (trash.getTrashElec() != null ? trash.getTrashElec() : 0));
                adminAccum.setAccumtrashEtc(adminAccum.getAccumtrashEtc() + (trash.getTrashEtc() != null ? trash.getTrashEtc() : 0));
                adminAccum.setAccumUpdatedAt(LocalDateTime.now());

                adminAccumRepository.save(adminAccum);
            } catch (Exception e) {
                log.error("관리자 ID: {} 누적 데이터 증분 업데이트 중 오류 발생", admin.getAdminId(), e);
            }
        }

        log.info("모든 관리자 누적 데이터 증분 업데이트 완료");
    }

    /**
     * 제보 생성 시 관리자 누적 데이터 증분 업데이트
     */
    @Transactional
    public void incrementAdminNotifyCount() {
        log.info("관리자 누적 데이터 제보 수 증분 업데이트 시작");

        List<Admin> admins = adminRepository.findAll();

        for (Admin admin : admins) {
            try {
                AdminAccum adminAccum = adminAccumRepository.findByAdmin(admin)
                        .orElseGet(() -> {
                            AdminAccum newAccum = AdminAccum.builder()
                                    .admin(admin)
                                    .accumKg(0.0)
                                    .accumL(0.0)
                                    .accumAct(0)
                                    .accumOrg(0)
                                    .accumNotify(0)
                                    .accumUser(0)
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
                            return adminAccumRepository.save(newAccum);
                        });

                // 제보 수 증가
                adminAccum.setAccumNotify(adminAccum.getAccumNotify() + 1);
                adminAccum.setAccumUpdatedAt(LocalDateTime.now());

                adminAccumRepository.save(adminAccum);
            } catch (Exception e) {
                log.error("관리자 ID: {} 제보 수 증분 업데이트 중 오류 발생", admin.getAdminId(), e);
            }
        }

        log.info("모든 관리자 제보 수 증분 업데이트 완료");
    }

    /**
     * 회원가입 시 관리자 누적 데이터 증분 업데이트
     */
    @Transactional
    public void incrementAdminUserCount() {
        log.info("관리자 누적 데이터 회원 수 증분 업데이트 시작");

        List<Admin> admins = adminRepository.findAll();

        for (Admin admin : admins) {
            try {
                AdminAccum adminAccum = adminAccumRepository.findByAdmin(admin)
                        .orElseGet(() -> {
                            AdminAccum newAccum = AdminAccum.builder()
                                    .admin(admin)
                                    .accumKg(0.0)
                                    .accumL(0.0)
                                    .accumAct(0)
                                    .accumOrg(0)
                                    .accumNotify(0)
                                    .accumUser(0)
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
                            return adminAccumRepository.save(newAccum);
                        });

                // 회원 수 증가
                adminAccum.setAccumUser(adminAccum.getAccumUser() + 1);
                adminAccum.setAccumUpdatedAt(LocalDateTime.now());

                adminAccumRepository.save(adminAccum);
            } catch (Exception e) {
                log.error("관리자 ID: {} 회원 수 증분 업데이트 중 오류 발생", admin.getAdminId(), e);
            }
        }

        log.info("모든 관리자 회원 수 증분 업데이트 완료");
    }
}
