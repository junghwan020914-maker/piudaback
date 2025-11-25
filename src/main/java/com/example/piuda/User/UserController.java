package com.example.piuda.User;

import com.example.piuda.domain.DTO.UserDTO;
import com.example.piuda.User.UserService;
import com.example.piuda.User.UserService.LoginRequest;
import com.example.piuda.User.UserService.TokenResponse;
import com.example.piuda.User.UserService.MeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.piuda.security.TurnstileService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final TurnstileService turnstileService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDTO dto) {
        if (!turnstileService.verifyToken(dto.getTurnstileToken())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Bot verification failed"));
        }

        userService.signup(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (!turnstileService.verifyToken(req.getTurnstileToken())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Bot verification failed"));
        }
        return ResponseEntity.ok(userService.login(req));
    }

    // 토큰 테스트용: Authorization: Bearer <token>
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // 이 엔드포인트는 "내 정보" 조회니까,
            // 로그인 안 했으면 401(Unauthorized) 주는 게 자연스러움
            return ResponseEntity.status(401).build();
        }

        // authentication.getName() 에 이메일을 넣도록 설정할 것 (JwtTokenProvider 참조)
        return ResponseEntity.ok(userService.me(authentication.getName()));
    }
}
