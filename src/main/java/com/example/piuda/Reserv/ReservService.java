package com.example.piuda.Reserv;

import com.example.piuda.domain.Entity.Reserv;
import com.example.piuda.Pin.PinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservService {
    private final PinService pinService;

    // 예약 생성 시 핀 색상 파란색으로 변경
    public boolean reserveAndSetPinBlue(Reserv reserv) {
        boolean result = pinService.setPinColorBlue(reserv.getPin().getPinId());
        // 예약 저장 로직은 필요시 추가
        return result;
    }
}
