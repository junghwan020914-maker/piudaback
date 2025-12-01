package com.example.piuda.Notify;

import com.example.piuda.domain.Entity.Notify;
import com.example.piuda.domain.Entity.Notify.NotifyStatus;
import com.example.piuda.domain.Entity.Pin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotifyRepository extends JpaRepository<Notify, Long> {
    List<Notify> findByNotifyStatus(NotifyStatus status);
    List<Notify> findByPin(Pin pin);
    List<Notify> findByPinAndNotifyStatus(Pin pin, Notify.NotifyStatus status);
    
    // WAIT 상태의 제보를 최신순으로 조회
    List<Notify> findByNotifyStatusOrderByNotifyCreatedAtDesc(NotifyStatus status);
}
