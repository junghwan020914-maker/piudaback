package com.example.piuda.User;

import com.example.piuda.domain.DTO.UserDTO;
import com.example.piuda.domain.Entity.User;
import com.example.piuda.User.UserRepository;
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

    public void signup(UserDTO dto) {
        if (userRepository.existsByUserEmail(dto.getUserEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .userName(dto.getUserName())
                .userEmail(dto.getUserEmail())
                .userPw(passwordEncoder.encode(dto.getUserPw()))
                .userPhone(dto.getUserPhone())
                // userRole은 엔티티에서 @Builder.Default 로 PRIVATE
                .build();
        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByUserEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(req.getPassword(), user.getUserPw())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        String accessToken = jwtTokenProvider.createToken(user.getUserId(), user.getUserEmail(), user.getUserRole().name());
        return new TokenResponse(accessToken, "Bearer");
    }

    // 로그인 확인용(예시)
    public MeResponse me(String email) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new MeResponse(user.getUserId(), user.getUserName(), user.getUserEmail(), user.getUserPhone(), user.getUserRole().name());
    }

    // --- DTOs for auth ---
    public static record LoginRequest(String email, String password) {
        public String getEmail() {
            return email;
        }
        public String getPassword() {
            return password;
        }
    }
    public static record TokenResponse(String accessToken, String tokenType) {}
    public static record MeResponse(Long userId, String userName, String userEmail, String userPhone, String role) {}
}
