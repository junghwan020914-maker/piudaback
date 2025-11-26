package com.example.piuda.PrivateActivity;

import com.example.piuda.domain.Entity.PrivateActivity;
import com.example.piuda.domain.Entity.Report;
import com.example.piuda.domain.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateActivityRepository extends JpaRepository<PrivateActivity, Long> {
    // 사용자의 모든 관심 활동 조회
    List<PrivateActivity> findByUser(User user);
    
    // 사용자의 관심 활동을 최신순으로 조회
    List<PrivateActivity> findByUserOrderByCreatedAtDesc(User user);
    
    // 특정 User와 Report의 좋아요 관계 조회
    Optional<PrivateActivity> findByUserAndReport(User user, Report report);
    
    // 특정 User가 특정 Report에 좋아요를 눌렀는지 확인
    boolean existsByUserAndReport(User user, Report report);
    
    // Report의 좋아요 개수 조회
    long countByReport(Report report);
}
