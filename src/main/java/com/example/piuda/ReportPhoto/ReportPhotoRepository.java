package com.example.piuda.ReportPhoto;

import com.example.piuda.domain.Entity.Report;
import com.example.piuda.domain.Entity.ReportPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportPhotoRepository extends JpaRepository<ReportPhoto, Long> {
	List<ReportPhoto> findByReport(Report report);
}
