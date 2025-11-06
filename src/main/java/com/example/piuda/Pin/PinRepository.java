package com.example.piuda.Pin;

import com.example.piuda.domain.Entity.Pin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PinRepository extends JpaRepository<Pin, Long> {
}
