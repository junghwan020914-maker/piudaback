package com.example.piuda.MainPage;

import com.example.piuda.Report.ReportRepository;
import com.example.piuda.domain.DTO.ReportResponseDTO;
import com.example.piuda.domain.Entity.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainpageService {

    private final ReportRepository reportRepository;

    /**
     * 최근 활동 후기 3개를 반환
     */
    @Transactional(readOnly = true)
    public List<ReportResponseDTO.MainPageDTO> getRecentReports() {
        // reportId 기준으로 내림차순 정렬하여 최근 3개 조회
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "reportId"));
        
        List<Report> recentReports = reportRepository.findAll(pageRequest).getContent();
        
        return recentReports.stream()
                .map(ReportResponseDTO.MainPageDTO::from)
                .collect(Collectors.toList());
    }
}
