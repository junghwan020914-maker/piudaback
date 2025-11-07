package com.example.piuda.ReportPhoto;

import com.example.piuda.domain.Entity.ReportPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportPhotoRepository extends JpaRepository<ReportPhoto, Long> {
    // 특정 Report에 속한 모든 사진 찾기
    List<ReportPhoto> findByReportReportId(Long reportId);
}
