package com.example.piuda.Dashboard;

import com.example.piuda.Admin.AdminRepository;
import com.example.piuda.AdminAccum.AdminAccumRepository;
import com.example.piuda.Notify.NotifyRepository;
import com.example.piuda.NotifyPhoto.NotifyPhotoRepository;
import com.example.piuda.Org.OrgRepository;
import com.example.piuda.OrgAccum.OrgAccumRepository;
import com.example.piuda.Report.ReportRepository;
import com.example.piuda.ReportPhoto.ReportPhotoRepository;
import com.example.piuda.User.UserRepository;
import com.example.piuda.domain.DTO.DashboardResponseDTO;
import com.example.piuda.domain.DTO.ReportResponseDTO;
import com.example.piuda.domain.Entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrgRepository orgRepository;
    private final OrgAccumRepository orgAccumRepository;
    private final ReportRepository reportRepository;
    private final ReportPhotoRepository reportPhotoRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AdminAccumRepository adminAccumRepository;
    private final NotifyRepository notifyRepository;
    private final NotifyPhotoRepository notifyPhotoRepository;

    /**
     * 단체 대시보드 데이터 조회
     * @param email 로그인한 사용자의 이메일
     * @return 단체 대시보드 데이터
     */
    @Transactional(readOnly = true)
    public DashboardResponseDTO.OrgDashboardDTO getOrgDashboard(String email) {
        // 1. 이메일로 User 조회
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 2. User로 Org 조회
        Org org = orgRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("단체 정보를 찾을 수 없습니다."));
        
        // 3. Org로 OrgAccum 조회
        OrgAccum orgAccum = orgAccumRepository.findByOrg(org)
                .orElseThrow(() -> new IllegalArgumentException("누적 데이터를 찾을 수 없습니다."));
        
        // 4. 해당 단체가 작성한 후기 목록 조회
        List<Report> reports = reportRepository.findByOrg(org);
        
        // 5. Report를 ReportResponseDTO로 변환
        List<ReportResponseDTO> reportDTOs = reports.stream()
                .map(report -> {
                    List<String> photoUrls = reportPhotoRepository.findByReport(report)
                            .stream()
                            .map(photo -> photo.getRphotoPath())
                            .collect(Collectors.toList());
                    return new ReportResponseDTO(report, photoUrls);
                })
                .collect(Collectors.toList());
        
        // 6. 월별 통계 데이터 생성 (현재 년도 기준 1월~12월)
        int currentYear = LocalDateTime.now().getYear();
        List<DashboardResponseDTO.MonthlyStatsDTO> monthlyStats = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            Double kg = reportRepository.sumKgByOrgAndMonth(org, currentYear, month);
            Long reportCount = reportRepository.countByOrgAndMonth(org, currentYear, month);
            
            monthlyStats.add(DashboardResponseDTO.MonthlyStatsDTO.builder()
                    .month(month)
                    .kg(kg != null ? kg : 0.0)
                    .reportCount(reportCount != null ? reportCount : 0L)
                    .build());
        }
        
        // 7. DashboardResponseDTO 생성 및 반환
        return DashboardResponseDTO.OrgDashboardDTO.from(orgAccum, reportDTOs, monthlyStats);
    }

    /**
     * 관리자 대시보드 데이터 조회
     * @param email 로그인한 사용자의 이메일
     * @return 관리자 대시보드 데이터
     */
    @Transactional(readOnly = true)
    public DashboardResponseDTO.AdminDashboardDTO getAdminDashboard(String email) {
        // 1. 이메일로 User 조회
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 2. User로 Admin 조회
        Admin admin = adminRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));
        
        // 3. Admin으로 AdminAccum 조회
        AdminAccum adminAccum = adminAccumRepository.findByAdmin(admin)
                .orElseThrow(() -> new IllegalArgumentException("누적 데이터를 찾을 수 없습니다."));
        
        // 4. 모든 제보 목록 조회
        List<Notify> notifies = notifyRepository.findAll();
        
        // 5. Notify를 NotifyDTO로 변환
        List<DashboardResponseDTO.NotifyDTO> notifyDTOs = notifies.stream()
                .map(notify -> {
                    List<String> photoUrls = notifyPhotoRepository.findByNotify(notify)
                            .stream()
                            .map(photo -> photo.getNphotoPath())
                            .collect(Collectors.toList());
                    return DashboardResponseDTO.NotifyDTO.from(notify, photoUrls);
                })
                .collect(Collectors.toList());
        
        // 6. 월별 통계 데이터 생성 (현재 년도 기준 1월~12월, 전체 사용자 후기 기준)
        int currentYear = LocalDateTime.now().getYear();
        List<DashboardResponseDTO.MonthlyStatsDTO> monthlyStats = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            Double kg = reportRepository.sumTotalKgByMonth(currentYear, month);
            Long reportCount = reportRepository.countTotalByMonth(currentYear, month);
            
            monthlyStats.add(DashboardResponseDTO.MonthlyStatsDTO.builder()
                    .month(month)
                    .kg(kg != null ? kg : 0.0)
                    .reportCount(reportCount != null ? reportCount : 0L)
                    .build());
        }
        
        // 7. DashboardResponseDTO 생성 및 반환
        return DashboardResponseDTO.AdminDashboardDTO.from(adminAccum, notifyDTOs, monthlyStats);
    }

    /**
     * 개인 대시보드 데이터 조회
     * @param email 로그인한 사용자의 이메일
     * @return 개인 대시보드 데이터
     */
    @Transactional(readOnly = true)
    public DashboardResponseDTO.PrivateDashboardDTO getPrivateDashboard(String email) {
        // 1. 이메일로 User 조회
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 2. USER_ROLE이 PRIVATE인지 확인
        if (user.getUserRole() != User.UserRole.PRIVATE) {
            throw new IllegalArgumentException("개인 회원만 접근 가능합니다.");
        }
        
        // 3. 유저가 작성한 후기의 report_id 목록
        List<Long> writtenReportIds = reportRepository.findReportIdsByWriter(user);
        
        // 4. 유저가 좋아요한 후기의 report_id 목록
        List<Long> likedReportIds = reportRepository.findReportIdsByLikedUser(user);
        
        // 5. 두 목록 합치기 (중복 제거를 위해 Set 사용)
        Set<Long> allReportIds = new HashSet<>();
        allReportIds.addAll(writtenReportIds);
        allReportIds.addAll(likedReportIds);
        
        // 6. 관련 후기 목록 생성 (ID와 대표 사진만)
        List<DashboardResponseDTO.SimpleReportDTO> simpleReportDTOs = new ArrayList<>();
        
        for (Long reportId : allReportIds) {
            Report report = reportRepository.findById(reportId).orElse(null);
            if (report != null) {
                // 첫 번째 사진만 가져오기
                String photoUrl = reportPhotoRepository.findByReport(report)
                        .stream()
                        .findFirst()
                        .map(photo -> photo.getRphotoPath())
                        .orElse(null);
                
                simpleReportDTOs.add(DashboardResponseDTO.SimpleReportDTO.builder()
                        .reportId(reportId)
                        .photoUrl(photoUrl)
                        .build());
            }
        }
        
        // 7. 월별 활동 통계 계산: 작성한 후기 + 좋아요한 후기 (중복 제거)
        int currentYear = LocalDateTime.now().getYear();
        List<DashboardResponseDTO.MonthlyActivityStatsDTO> monthlyStats = new ArrayList<>();
        
        // 월별로 통계 계산
        for (int month = 1; month <= 12; month++) {
            Long activityCount = 0L;
            
            if (!allReportIds.isEmpty()) {
                activityCount = reportRepository.countDistinctByReportIdsAndMonth(
                    new ArrayList<>(allReportIds), currentYear, month);
            }
            
            monthlyStats.add(DashboardResponseDTO.MonthlyActivityStatsDTO.builder()
                    .month(month)
                    .activityCount(activityCount != null ? activityCount : 0L)
                    .build());
        }
        
        // 8. PrivateDashboardDTO 생성 및 반환
        return DashboardResponseDTO.PrivateDashboardDTO.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .reports(simpleReportDTOs)
                .monthlyStats(monthlyStats)
                .build();
    }
}
