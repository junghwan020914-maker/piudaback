package com.example.piuda.Notify;

import com.example.piuda.domain.Entity.Notify;
import com.example.piuda.domain.Entity.Notify.NotifyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotifyRepository extends JpaRepository<Notify, Long> {
    List<Notify> findByNotifyStatus(NotifyStatus status);
}
