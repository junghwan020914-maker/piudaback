package com.example.piuda.Notify;

import com.example.piuda.Pin.PinService;
import com.example.piuda.domain.Entity.Notify;
import com.example.piuda.domain.Entity.Notify.NotifyStatus;
import com.example.piuda.domain.Entity.Pin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifyService {
    private final NotifyRepository notifyRepository;
    private final PinService pinService;

    @Transactional
    public void processWaitNotifies(double distanceMeters) {
        List<Notify> waits = notifyRepository.findByNotifyStatus(NotifyStatus.WAIT);
        for (Notify n : waits) {
            // pin이 이미 참조되어 있으면 스킵
            if (n.getPin() != null) continue;
            Double x = n.getNotifyX();
            Double y = n.getNotifyY();
            pinService.createPinIfNotNearby(x, y, distanceMeters).ifPresent(pin -> {
                n.setPin(pin);
                notifyRepository.save(n);
            });
        }
    }
}
