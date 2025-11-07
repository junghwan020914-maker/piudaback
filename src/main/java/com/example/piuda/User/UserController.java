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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDTO dto) {
        userService.signup(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(userService.login(req));
    }

    // 토큰 테스트용: Authorization: Bearer <token>
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        // authentication.getName() 에 이메일을 넣도록 설정할 것 (JwtTokenProvider 참조)
        return ResponseEntity.ok(userService.me(authentication.getName()));
    }
}
