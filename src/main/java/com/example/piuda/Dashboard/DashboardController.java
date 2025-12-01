package com.example.piuda.Dashboard;

import com.example.piuda.AdminAccum.AdminAccumService;
import com.example.piuda.OrgAccum.OrgAccumService;
import com.example.piuda.domain.DTO.DashboardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final OrgAccumService orgAccumService;
    private final AdminAccumService adminAccumService;
    private final ExcelService excelService;

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
        orgAccumService.updateOrgAccumulation();
        return ResponseEntity.ok("단체 누적 데이터가 업데이트되었습니다.");
    }

    /**
     * 관리자 누적 데이터 수동 업데이트
     * 테스트 또는 즉시 업데이트가 필요한 경우 사용
     */
    @PostMapping("/admin/accum/update")
    public ResponseEntity<String> updateAdminAccumulation() {
        adminAccumService.updateAdminAccumulation();
        return ResponseEntity.ok("관리자 누적 데이터가 업데이트되었습니다.");
    }

    /**
     * 개인 대시보드 조회
     * 로그인한 개인 회원의 좋아요한 후기 목록 및 월별 통계 반환
     * 
     * @param authentication Spring Security가 자동으로 주입하는 인증 정보
     * @return 개인 대시보드 데이터
     */
    @GetMapping("/private")
    public ResponseEntity<DashboardResponseDTO.PrivateDashboardDTO> getPrivateDashboard(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.getPrivateDashboard(email));
    }

    /**
     * 단체 대시보드 - 후기 엑셀 다운로드
     * 해당 단체가 작성한 후기 데이터를 Trash, 좌표 정보와 함께 엑셀 파일로 다운로드
     * 
     * @param authentication Spring Security가 자동으로 주입하는 인증 정보
     * @return 엑셀 파일 바이트 배열
     * @throws IOException 엑셀 생성 중 오류 발생 시
     */
    @GetMapping("/org/excel")
    public ResponseEntity<byte[]> downloadOrgReportsExcel(Authentication authentication) throws IOException {
        if (authentication == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String email = authentication.getName();
        byte[] excelData = excelService.generateOrgReportsExcel(email);
        
        String filename = "org_reports_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    /**
     * 관리자 대시보드 - 후기 엑셀 다운로드
     * 모든 후기 데이터를 Trash, 단체, 좌표 정보와 함께 엑셀 파일로 다운로드
     * 
     * @param authentication Spring Security가 자동으로 주입하는 인증 정보
     * @return 엑셀 파일 바이트 배열
     * @throws IOException 엑셀 생성 중 오류 발생 시
     */
    @GetMapping("/admin/excel")
    public ResponseEntity<byte[]> downloadReportsExcel(Authentication authentication) throws IOException {
        if (authentication == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        byte[] excelData = excelService.generateReportsExcel();
        
        String filename = "reports_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }
}
