package com.example.piuda.Notify;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
