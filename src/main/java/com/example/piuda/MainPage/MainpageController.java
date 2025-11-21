package com.example.piuda.MainPage;

import com.example.piuda.domain.DTO.ReportResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mainpage")
@RequiredArgsConstructor
public class MainpageController {

    private final MainpageService mainpageService;

    /**
     * 메인페이지용 최근 활동 후기 3개 조회
     */
    @GetMapping
    public ResponseEntity<List<ReportResponseDTO.MainPageDTO>> getRecentReports() {
        List<ReportResponseDTO.MainPageDTO> recentReports = mainpageService.getRecentReports();
        return ResponseEntity.ok(recentReports);
    }
}
