package com.example.piuda.Dashboard;

import com.example.piuda.domain.DTO.DashboardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

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
}
