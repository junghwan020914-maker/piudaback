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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final PinRepository pinRepository;
    private final TrashRepository trashRepository;
    private final ReportPhotoRepository reportPhotoRepository;
    
  //  @Value("${file.upload.dir}")
    private String uploadDir;

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
                try {
                    String photoPath = savePhotoFile(photo);
                    
                    ReportPhoto reportPhoto = ReportPhoto.builder()
                            .report(savedReport)
                            .rphotoPath(photoPath)
                            .build();
                    reportPhotoRepository.save(reportPhoto);
                } catch (IOException e) {
                    throw new RuntimeException("사진 저장 중 오류가 발생했습니다.", e);
                }
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

    private String savePhotoFile(MultipartFile photo) throws IOException {
        // 업로드 디렉토리가 없으면 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 고유한 파일명 생성 (UUID + 타임스탬프 + 원본 파일명)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String originalFilename = photo.getOriginalFilename();
        String extension = "";
        
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String newFilename = timestamp + "_" + uuid + extension;
        
        // 파일 저장
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // DB에 저장할 상대 경로 반환
        return uploadDir + "/" + newFilename;
    }
}
