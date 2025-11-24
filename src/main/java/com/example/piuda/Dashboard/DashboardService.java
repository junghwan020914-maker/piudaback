package com.example.piuda.Dashboard;

import com.example.piuda.Org.OrgRepository;
import com.example.piuda.OrgAccum.OrgAccumRepository;
import com.example.piuda.Report.ReportRepository;
import com.example.piuda.ReportPhoto.ReportPhotoRepository;
import com.example.piuda.User.UserRepository;
import com.example.piuda.domain.DTO.DashboardResponseDTO;
import com.example.piuda.domain.DTO.ReportResponseDTO;
import com.example.piuda.domain.Entity.Org;
import com.example.piuda.domain.Entity.OrgAccum;
import com.example.piuda.domain.Entity.Report;
import com.example.piuda.domain.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrgRepository orgRepository;
    private final OrgAccumRepository orgAccumRepository;
    private final ReportRepository reportRepository;
    private final ReportPhotoRepository reportPhotoRepository;
    private final UserRepository userRepository;

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
        
        // 6. DashboardResponseDTO 생성 및 반환
        return DashboardResponseDTO.OrgDashboardDTO.from(orgAccum, reportDTOs);
    }
}
