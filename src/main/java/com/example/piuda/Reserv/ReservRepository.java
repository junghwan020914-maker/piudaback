package com.example.piuda.Reserv;

import com.example.piuda.domain.Entity.Pin;
import com.example.piuda.domain.Entity.Reserv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservRepository extends JpaRepository<Reserv, Long> {
    List<Reserv> findByReservDateBefore(LocalDate date);
    List<Reserv> findByPin(Pin pin);
    long deleteByPin(Pin pin);
}
