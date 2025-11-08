package com.example.piuda.Notify;

import com.example.piuda.Pin.PinService;
import com.example.piuda.domain.DTO.NotifyCreateRequestDTO;
import com.example.piuda.domain.Entity.Notify;
import com.example.piuda.domain.Entity.Notify.NotifyStatus;
import com.example.piuda.domain.Entity.NotifyPhoto;
import com.example.piuda.domain.Entity.Pin;
//import com.example.piuda.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotifyService {
    private final NotifyRepository notifyRepository;
    private final PinService pinService;
    private final com.example.piuda.NotifyPhoto.NotifyPhotoRepository notifyPhotoRepository;
   // private final StorageService storageService;

    // 핀 중복 판별 거리(임시 하드코딩)
    private static final double NEARBY_DISTANCE_METERS = 50.0;



    // WAIT -> ACCEPT 전환 시, 핀 자동 연결/생성 처리
    @Transactional
    public boolean acceptNotify(Long notifyId) {
        Optional<Notify> opt = notifyRepository.findById(notifyId);
        if (opt.isEmpty()) return false;
        Notify n = opt.get();

        // 상태 전환: WAIT인 경우 ACCEPT로 변경(이미 ACCEPT여도 핀 연결만 시도)
        if (n.getNotifyStatus() == NotifyStatus.WAIT) {
            n.setNotifyStatus(NotifyStatus.ACCEPT);
        }

        // 이미 핀이 연결되어 있으면 종료 (상태 변경은 트랜잭션 커밋 시 반영됨)
        if (n.getPin() != null) {
            return true;
        }

        Double x = n.getNotifyX();
        Double y = n.getNotifyY();
        
        // addPinFromNotify 스타일로: 근처에 없으면 생성, 있으면 기존 핀 반환 (거리 하드코딩 사용)
        Pin pin = pinService.addPinFromNotifyAndGet(x, y, NEARBY_DISTANCE_METERS).orElse(null);

        if (pin == null) return false; // 생성 실패 등 비정상 상황

        n.setPin(pin);
        notifyRepository.save(n);
        return true;
    }

    // 제보 생성: 바로 ACCEPT로 저장하고, 즉시 빨간 핀 생성/연결, 사진 업로드까지 처리
    @Transactional
    public Long createAndAccept(NotifyCreateRequestDTO dto, List<MultipartFile> photos) {
        // 1) 핀 확보 (근처 없으면 RED 생성)
        double x = dto.getX();
        double y = dto.getY();
        Pin pin = pinService.addPinFromNotifyAndGet(x, y, NEARBY_DISTANCE_METERS).orElse(null);
        if (pin == null) {
            // addPinFromNotifyAndGet 내부에서 보통 생성되므로 null 가능성은 낮지만, 방어적으로 실패 처리
            throw new IllegalStateException("핀 생성에 실패했습니다.");
        }

        // 2) 제보 저장 (바로 ACCEPT)
        Notify notify = Notify.builder()
                .notifyX(x)
                .notifyY(y)
                .notifyCreatedAt(LocalDateTime.now())
                .notifyContent(dto.getContent())
                .notifyStatus(NotifyStatus.ACCEPT)
                .pin(pin)
                .build();
        Notify saved = notifyRepository.save(notify);

        // 3) 사진 업로드 및 연결
        /*if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                String url = storageService.upload("notifies", photo);
                NotifyPhoto np = NotifyPhoto.builder()
                        .notify(saved)
                        .nphotoPath(url)
                        .build();
                notifyPhotoRepository.save(np);
            }
        } */

        return saved.getNotifyId();
    }
}
