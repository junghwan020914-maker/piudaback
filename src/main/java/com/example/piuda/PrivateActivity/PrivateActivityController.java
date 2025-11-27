package com.example.piuda.PrivateActivity;

import com.example.piuda.User.UserRepository;
import com.example.piuda.domain.Entity.PrivateActivity;
import com.example.piuda.domain.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/private-activity")
@RequiredArgsConstructor
public class PrivateActivityController {

    private final PrivateActivityService privateActivityService;
    private final UserRepository userRepository;

    /**
     * 후기 좋아요 추가
     * PRIVATE 역할의 사용자만 가능
     * 
     * @param authentication Spring Security가 자동으로 주입하는 인증 정보
     * @param reportId 좋아요를 누를 후기 ID
     * @return 생성된 PrivateActivity
     */
    @PostMapping("/like/{reportId}")
    public ResponseEntity<PrivateActivity> addReportLike(
            Authentication authentication,
            @PathVariable Long reportId) {
        if (authentication == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String email = authentication.getName();
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        PrivateActivity privateActivity = privateActivityService.addReportLike(user.getUserId(), reportId);
        return ResponseEntity.ok(privateActivity);
    }

    /**
     * 후기 좋아요 취소
     * 
     * @param authentication Spring Security가 자동으로 주입하는 인증 정보
     * @param reportId 좋아요를 취소할 후기 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/like/{reportId}")
    public ResponseEntity<String> removeReportLike(
            Authentication authentication,
            @PathVariable Long reportId) {
        if (authentication == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String email = authentication.getName();
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        privateActivityService.removeReportLike(user.getUserId(), reportId);
        return ResponseEntity.ok("좋아요가 취소되었습니다.");
    }

    /**
     * 후기 좋아요 여부 확인
     * 로그인한 사용자가 특정 후기에 좋아요를 눌렀는지 확인
     * 
     * @param authentication Spring Security가 자동으로 주입하는 인증 정보
     * @param reportId 확인할 후기 ID
     * @return 좋아요 여부 (true/false)
     */
    @GetMapping("/like/check/{reportId}")
    public ResponseEntity<Boolean> isReportLiked(
            Authentication authentication,
            @PathVariable Long reportId) {
        if (authentication == null) {
            return ResponseEntity.ok(false);
        }
        
        String email = authentication.getName();
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        boolean isLiked = privateActivityService.isReportLiked(user.getUserId(), reportId);
        return ResponseEntity.ok(isLiked);
    }
}
