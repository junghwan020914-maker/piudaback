package com.example.piuda.Notify;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotifyController {
    private final NotifyService notifyService;

    // 수동으로 트리거: 예) /api/notify/process-wait?distance=50
    @GetMapping("/process-wait")
    public String processWait(@RequestParam(defaultValue = "50") double distance) {
        notifyService.processWaitNotifies(distance);
        return "처리 완료";
    }
}
