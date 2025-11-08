package com.example.piuda.NotifyPhoto;

import com.example.piuda.domain.Entity.Notify;
import com.example.piuda.domain.Entity.NotifyPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotifyPhotoRepository extends JpaRepository<NotifyPhoto, Long> {
    List<NotifyPhoto> findByNotify(Notify notify);
}
