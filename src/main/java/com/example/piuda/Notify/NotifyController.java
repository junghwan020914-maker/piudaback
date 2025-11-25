package com.example.piuda.Notify;

import com.example.piuda.domain.DTO.NotifyCreateRequestDTO;
import com.example.piuda.domain.DTO.NotifyCreateResponseDTO;
import com.example.piuda.security.TurnstileService;
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
    private final TurnstileService turnstileService;

    // WAIT -> ACCEPT 상태 변경과 동시에 핀 자동 연결/생성
    @PostMapping("/{id}/accept")
    public String accept(@PathVariable Long id) {
        boolean ok = notifyService.acceptNotify(id);
        return ok ? "ACCEPT 처리 및 핀 업데이트 완료" : "대상 없음 또는 처리 실패";
    }
    // WAIT -> REJECT 상태 변경
    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id) {
        boolean ok = notifyService.rejectNotify(id);
        return ok ? "reject 처리 완료" : "대상 없음 또는 처리 실패";
    }

    // 제보 생성: 바로 ACCEPT 저장 + RED 핀 생성/연결 + 사진 업로드
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<NotifyCreateResponseDTO> create(
            @RequestPart("payload") NotifyCreateRequestDTO payload,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        // Turnstile 검증 추가
        if (!turnstileService.verifyToken(payload.getTurnstileToken())) {
            return ResponseEntity.badRequest().build(); // body 없이
            // 또는 에러용 DTO 반환
        }

        Long notifyId = notifyService.createNotify(payload, photos);
        // notifyId로 핀ID, 사진URL 목록 조회
        NotifyCreateResponseDTO dto = notifyService.buildCreateResponse(notifyId);
        return ResponseEntity.ok(dto);
    }
}
