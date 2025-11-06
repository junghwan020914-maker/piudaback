package com.example.piuda.Reserv;

import com.example.piuda.domain.Entity.Reserv;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reserv")
@RequiredArgsConstructor
public class ReservController {
    private final ReservService reservService;

    @PostMapping
    public String reserve(@RequestBody Reserv reserv) {
        boolean result = reservService.reserveAndSetPinBlue(reserv);
        return result ? "예약 및 핀 색상 변경 성공" : "핀을 찾을 수 없습니다";
    }
}
