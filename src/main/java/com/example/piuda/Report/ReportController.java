package com.example.piuda.Report;

import com.example.piuda.domain.DTO.ReportRequestDTO;
import com.example.piuda.domain.DTO.ReportResponseDTO;
import com.example.piuda.security.TurnstileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {
    
    private final ReportService reportService;
    private final TurnstileService turnstileService;

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestPart(value = "report") ReportRequestDTO reportRequestDTO,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            Authentication authentication
            ) {

        // Turnstile 검증 추가
        if (!turnstileService.verifyToken(reportRequestDTO.getTurnstileToken())) {
            return ResponseEntity.badRequest().body("봇 검증에 실패했습니다. 다시 시도해주세요.");
        }

        // 각 이미지 파일 크기 검증 (10MB 제한)
        if (photos != null) {
            for (MultipartFile photo : photos) {
                if (photo.getSize() > 10 * 1024 * 1024) { // 10MB in bytes
                    return ResponseEntity.badRequest().body("각 이미지 파일은 10MB를 초과할 수 없습니다.");
                }
            }
        }

        return ResponseEntity.ok(reportService.createReport(reportRequestDTO, photos, authentication));
    }

    @GetMapping
    public ResponseEntity<List<ReportResponseDTO>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReportsWithPhotos());
    }
}
