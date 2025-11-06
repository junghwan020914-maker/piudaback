package com.example.piuda.Report;

import com.example.piuda.domain.Entity.Pin;
import com.example.piuda.domain.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPin(Pin pin);
}