package com.example.piuda.Report;

import com.example.piuda.Pin.PinRepository;
import com.example.piuda.ReportPhoto.ReportPhotoRepository;
import com.example.piuda.Trash.TrashRepository;
import com.example.piuda.domain.DTO.ReportRequestDTO;
import com.example.piuda.domain.DTO.ReportResponseDTO;
import com.example.piuda.domain.Entity.Pin;
import com.example.piuda.domain.Entity.Report;
import com.example.piuda.domain.Entity.ReportPhoto;
import com.example.piuda.domain.Entity.Trash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.piuda.storage.StorageService;
import com.example.piuda.storage.StorageFolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final PinRepository pinRepository;
    private final TrashRepository trashRepository;
    private final ReportPhotoRepository reportPhotoRepository;
    
        private final StorageService storageService;

    public Long createReport(ReportRequestDTO dto, List<MultipartFile> photos) {
        // 1. Pin 엔티티 생성
        Pin pin = Pin.builder()
                .pinX(dto.getPinX())
                .pinY(dto.getPinY())
                .pinCreatedAt(LocalDateTime.now())
                .build();
        pinRepository.save(pin);

        // 2. Trash 엔티티 생성 (kg, L 분리)
        Trash.TrashBuilder trashBuilder = Trash.builder();
        trashBuilder.trashKg(dto.getTrashKg() != null ? dto.getTrashKg() : 0.0);
        trashBuilder.trashL(dto.getTrashL() != null ? dto.getTrashL() : 0.0);

        // 선택적 쓰레기 종류별 개수 설정 (DTO 필드명을 엔티티와 동일하게 사용)
        Trash trash = trashBuilder
            .trashPet(dto.getTrashPet())
            .trashBag(dto.getTrashBag())
            .trashNet(dto.getTrashNet())
            .trashGlass(dto.getTrashGlass())
            .trashCan(dto.getTrashCan())
            .trashRope(dto.getTrashRope())
            .trashCloth(dto.getTrashCloth())
            .trashElec(dto.getTrashElec())
            .trashEtc(dto.getTrashEtc())
            .build();

        trashRepository.save(trash);

        // 3. Report 엔티티 생성
        Report report = Report.builder()
                .reportName(dto.getReportName())
                .reportPeople(dto.getReportPeople())
                .reportTitle(dto.getReportTitle())
                .reportDate(dto.getReportDate())
                .reportDetailLocation(dto.getReportDetailLocation())
                .reportContent(dto.getReportContent())
                .pin(pin)
                .trash(trash)
                .build();
        
        Report savedReport = reportRepository.save(report);

        // 4. 사진이 있다면 ReportPhoto 엔티티들 생성
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                String url = storageService.upload(StorageFolder.REPORT, photo);
                ReportPhoto reportPhoto = ReportPhoto.builder()
                        .report(savedReport)
                        .rphotoPath(url)
                        .build();
                reportPhotoRepository.save(reportPhoto);
            }
        }

        return savedReport.getReportId();
    }
    
    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getAllReportsWithPhotos() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream().map(report -> {
            List<ReportPhoto> photos = reportPhotoRepository.findByReport(report);
            List<String> photoUrls = photos.stream()
                                           .map(ReportPhoto::getRphotoPath)
                                           .collect(Collectors.toList());
            return new ReportResponseDTO(report, photoUrls);
        }).collect(Collectors.toList());
    }

    // Local file save logic removed; now handled by S3StorageService.
}
