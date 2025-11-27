package com.example.piuda.User;

import com.example.piuda.domain.DTO.UserDTO;
import com.example.piuda.domain.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.piuda.config.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // === 기존 회원가입 ===
    public void signup(UserDTO dto) {
        if (userRepository.existsByUserEmail(dto.getUserEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .userName(dto.getUserName())
                .userEmail(dto.getUserEmail())
                .userPw(passwordEncoder.encode(dto.getUserPw()))
                .userPhone(dto.getUserPhone())
                .build();
        userRepository.save(user);
    }

    // === 기존 로그인 ===
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByUserEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(req.getPassword(), user.getUserPw())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        String accessToken = jwtTokenProvider.createToken(
                user.getUserId(), user.getUserEmail(), user.getUserRole().name()
        );
        return new TokenResponse(accessToken, "Bearer");
    }

    // === 로그인 유저 정보 조회 ===
    public MeResponse me(String email) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new MeResponse(
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserPhone(),
                user.getUserRole().name()
        );
    }

    // ✅ [추가] 카카오(OAuth) 전용 회원 생성 메서드
    public User registerOAuthUser(String name, String email, String phone, String rawPassword) {
        // 이미 있으면 그대로 반환 (안전장치용)
        return userRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .userName(name != null ? name : "USER")
                            .userEmail(email)
                            .userPw(passwordEncoder.encode(rawPassword)) // JWT/일반 로그인 대비해 인코딩
                            .userPhone(phone)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    // --- DTOs for auth ---
    public static record LoginRequest(String email, String password) {
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }
    public static record TokenResponse(String accessToken, String tokenType) {}
    public static record MeResponse(Long userId, String userName, String userEmail, String userPhone, String role) {}
}
