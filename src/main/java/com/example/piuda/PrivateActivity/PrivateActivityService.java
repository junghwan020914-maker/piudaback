package com.example.piuda.PrivateActivity;

import com.example.piuda.Report.ReportRepository;
import com.example.piuda.User.UserRepository;
import com.example.piuda.domain.Entity.PrivateActivity;
import com.example.piuda.domain.Entity.Report;
import com.example.piuda.domain.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrivateActivityService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final PrivateActivityRepository privateActivityRepository;

    /**
     * 후기 좋아요 추가 (관심 활동 등록)
     * PRIVATE 역할의 사용자만 가능
     * @param userId 로그인한 사용자 ID
     * @param reportId 좋아요를 누를 후기 ID
     * @return 생성된 PrivateActivity
     */
    @Transactional
    public PrivateActivity addReportLike(Long userId, Long reportId) {
        // 1. User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 2. PRIVATE 역할 확인
        if (user.getUserRole() != User.UserRole.PRIVATE) {
            throw new IllegalStateException("일반 사용자만 좋아요를 누를 수 있습니다.");
        }
        
        // 3. Report 조회
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다."));
        
        // 4. 중복 체크 (이미 좋아요한 경우)
        boolean alreadyLiked = privateActivityRepository.existsByUserAndReport(user, report);
        if (alreadyLiked) {
            throw new IllegalStateException("이미 좋아요를 누른 후기입니다.");
        }
        
        // 5. PrivateActivity 생성 및 저장
        PrivateActivity privateActivity = PrivateActivity.builder()
                .user(user)
                .report(report)
                .createdAt(LocalDateTime.now())
                .build();
        
        return privateActivityRepository.save(privateActivity);
    }

    /**
     * 후기 좋아요 취소 (관심 활동 삭제)
     * @param userId 로그인한 사용자 ID
     * @param reportId 좋아요를 취소할 후기 ID
     */
    @Transactional
    public void removeReportLike(Long userId, Long reportId) {
        // 1. User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 2. PRIVATE 역할 확인
        if (user.getUserRole() != User.UserRole.PRIVATE) {
            throw new IllegalStateException("일반 사용자만 좋아요를 취소할 수 있습니다.");
        }
        
        // 3. Report 조회
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다."));
        
        // 4. PrivateActivity 조회 및 삭제
        PrivateActivity privateActivity = privateActivityRepository.findByUserAndReport(user, report)
                .orElseThrow(() -> new IllegalArgumentException("좋아요 기록을 찾을 수 없습니다."));
        
        privateActivityRepository.delete(privateActivity);
    }

    /**
     * 사용자가 특정 후기에 좋아요를 눌렀는지 확인
     * @param userId 사용자 ID
     * @param reportId 후기 ID
     * @return 좋아요 여부
     */
    @Transactional(readOnly = true)
    public boolean isReportLiked(Long userId, Long reportId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        if (user.getUserRole() != User.UserRole.PRIVATE) {
            return false;
        }
        
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다."));
        
        return privateActivityRepository.existsByUserAndReport(user, report);
    }

    /**
     * 특정 후기의 좋아요 개수 조회
     * @param reportId 후기 ID
     * @return 좋아요 개수
     */
    @Transactional(readOnly = true)
    public long getReportLikeCount(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다."));
        
        return privateActivityRepository.countByReport(report);
    }
}
