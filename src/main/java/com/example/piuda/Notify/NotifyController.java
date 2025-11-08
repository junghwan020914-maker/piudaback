package com.example.piuda.Notify;

import com.example.piuda.domain.DTO.NotifyCreateRequestDTO;
import com.example.piuda.domain.DTO.NotifyCreateResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotifyController {
    private final NotifyService notifyService;


    // WAIT -> ACCEPT 상태 변경과 동시에 핀 자동 연결/생성
    @PostMapping("/{id}/accept")
    public String accept(@PathVariable Long id) {
        boolean ok = notifyService.acceptNotify(id);
        return ok ? "ACCEPT 처리 및 핀 업데이트 완료" : "대상 없음 또는 처리 실패";
    }

    // 제보 생성: 바로 ACCEPT 저장 + RED 핀 생성/연결 + 사진 업로드
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<NotifyCreateResponseDTO> create(
            @RequestPart("payload") NotifyCreateRequestDTO payload,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        Long notifyId = notifyService.createAndAccept(payload, photos);
        // 핀 ID와 사진 URL 목록 조회가 필요하다면 리포지토리 조회를 추가적으로 수행할 수 있으나,
        // 여기서는 간단히 notifyId만 반환하고, 클라이언트는 별도 조회 API로 상세 얻도록 한다.
        // 필요 시 확장 가능.
        return ResponseEntity.ok(new NotifyCreateResponseDTO(notifyId, null, "ACCEPT", null));
    }
}
