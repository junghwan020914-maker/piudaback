package com.example.piuda.Report;

import com.example.piuda.AdminAccum.AdminAccumService;
import com.example.piuda.Org.OrgRepository;
import com.example.piuda.OrgAccum.OrgAccumService;
import com.example.piuda.Pin.PinRepository;
import com.example.piuda.ReportPhoto.ReportPhotoRepository;
import com.example.piuda.Trash.TrashRepository;
import com.example.piuda.User.UserRepository;
import com.example.piuda.domain.DTO.ReportRequestDTO;
import com.example.piuda.domain.DTO.ReportResponseDTO;
import com.example.piuda.domain.Entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.piuda.storage.StorageService;
import com.example.piuda.storage.StorageFolder;
import com.example.piuda.Pin.PinService;
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
    private final PinService pinService;
    private final UserRepository userRepository;
    private final OrgRepository orgRepository;
    private final OrgAccumService orgAccumService;
    private final AdminAccumService adminAccumService;
    // 핀 중복 판별 거리(임시 하드코딩)
    private static final double NEARBY_DISTANCE_METERS = 500.0;

    public Long createReport(ReportRequestDTO dto, List<MultipartFile> photos, Authentication authentication) {
        // 해당위치 핀 있는경우 해당 핀 반환, 없는경우 핀 생성후 반환
        Pin pin = pinService.addPinFromReportAndGet(dto.getPinX(),dto.getPinY(),NEARBY_DISTANCE_METERS);
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

        // 3. 로그인 정보에 따라 writer와 writerType 설정
        User writer = null;
        Org org = null;
        Report.WriterType writerType = Report.WriterType.ANONYMOUS;
        
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            writer = userRepository.findByUserEmail(email).orElse(null);
            
            if (writer != null) {
                // 단체 계정인지 확인
                org = orgRepository.findByUser(writer).orElse(null);
                
                if (org != null) {
                    writerType = Report.WriterType.GROUP;
                } else {
                    writerType = Report.WriterType.PRIVATE;
                }
            }
        }

        // 4. Report 엔티티 생성
        Report report = Report.builder()
                .reportName(dto.getReportName())
                .reportPeople(dto.getReportPeople())
                .reportTitle(dto.getReportTitle())
                .reportDate(dto.getReportDate())
                .reportDetailLocation(dto.getReportDetailLocation())
                .reportContent(dto.getReportContent())
                .pin(pin)
                .trash(trash)
                .writer(writer)
                .writerType(writerType)
                .org(org)
                .build();
        
        Report savedReport = reportRepository.save(report);

        // 5. 사진이 있다면 ReportPhoto 엔티티들 생성
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

        // 6. 누적 데이터 증분 업데이트 (기존 값에 더하기)
        if (org != null) {
            // 단체 후기인 경우 해당 단체의 누적 데이터 증분 업데이트
            orgAccumService.incrementOrgAccumulation(org, savedReport);
        }
        // 관리자 누적 데이터는 항상 증분 업데이트 (전체 통계)
        adminAccumService.incrementAdminAccumulation(savedReport);

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

    @Transactional(readOnly = true)
    public List<ReportResponseDTO.ReportPageDTO> getAllReportsSimple() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream()
                .map(ReportResponseDTO.ReportPageDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportResponseDTO getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 후기를 찾을 수 없습니다."));
        List<ReportPhoto> photos = reportPhotoRepository.findByReport(report);
        List<String> photoUrls = photos.stream()
                .map(ReportPhoto::getRphotoPath)
                .collect(Collectors.toList());
        return new ReportResponseDTO(report, photoUrls);
    }

}
