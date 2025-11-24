package com.example.piuda.Dashboard;

import com.example.piuda.domain.DTO.DashboardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AccumulationService accumulationService;

    /**
     * 단체 대시보드 조회
     * 로그인한 단체 회원의 누적 데이터 및 작성한 후기 목록 반환
     * 
     * @param authentication Spring Security가 자동으로 주입하는 인증 정보
     * @return 단체 대시보드 데이터
     */
    @GetMapping("/org")
    public ResponseEntity<DashboardResponseDTO.OrgDashboardDTO> getOrgDashboard(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.getOrgDashboard(email));
    }

    /**
     * 관리자 대시보드 조회
     * 로그인한 관리자의 누적 데이터 및 모든 제보 목록 반환
     * 
     * @param authentication Spring Security가 자동으로 주입하는 인증 정보
     * @return 관리자 대시보드 데이터
     */
    @GetMapping("/admin")
    public ResponseEntity<DashboardResponseDTO.AdminDashboardDTO> getAdminDashboard(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.getAdminDashboard(email));
    }

    /**
     * 단체 누적 데이터 수동 업데이트
     * 테스트 또는 즉시 업데이트가 필요한 경우 사용
     */
    @PostMapping("/org/accum/update")
    public ResponseEntity<String> updateOrgAccumulation() {
        accumulationService.updateOrgAccumulation();
        return ResponseEntity.ok("단체 누적 데이터가 업데이트되었습니다.");
    }

    /**
     * 관리자 누적 데이터 수동 업데이트
     * 테스트 또는 즉시 업데이트가 필요한 경우 사용
     */
    @PostMapping("/admin/accum/update")
    public ResponseEntity<String> updateAdminAccumulation() {
        accumulationService.updateAdminAccumulation();
        return ResponseEntity.ok("관리자 누적 데이터가 업데이트되었습니다.");
    }
}
