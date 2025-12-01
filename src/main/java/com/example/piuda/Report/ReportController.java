package com.example.piuda.Report;

import com.example.piuda.domain.DTO.ReportRequestDTO;
import com.example.piuda.domain.DTO.ReportResponseDTO;
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

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestPart(value = "report") ReportRequestDTO reportRequestDTO,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            Authentication authentication
            ) {

        // 각 이미지 파일 크기 검증 (10MB 제한)
        if (photos != null) {
            for (MultipartFile photo : photos) {
                if (photo.getSize() > 10 * 1024 * 1024) { // 10MB in bytes
                    return ResponseEntity.badRequest()
                            .body("각 이미지 파일은 10MB를 초과할 수 없습니다.");
                }
            }
        }

        return ResponseEntity.ok(reportService.createReport(reportRequestDTO, photos, authentication));
    }

    @GetMapping
    public ResponseEntity<List<ReportResponseDTO.ReportPageDTO>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReportsSimple());
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponseDTO> getReportDetail(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReportDetail(reportId));
    }
}
