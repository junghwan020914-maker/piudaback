package com.example.piuda.config.oauth;

import com.example.piuda.domain.Entity.User;
import com.example.piuda.User.UserRepository;
import com.example.piuda.config.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;   // ✅ UserService 안 씀 → 순환 의존 X
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.oauth2.front-redirect-url}")
    private String frontRedirectUrl; // 예: http://125.6.40.169 혹은 프론트 도메인

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();

        // ⚠️ CustomOAuth2UserService 에서 attributes에 "email", "name"을 넣어준다고 가정
        String email = (String) principal.getAttributes().get("email");
        String name  = (String) principal.getAttributes().get("name");

        // 이메일은 엔티티에서 NOT NULL 이므로, 여기서 없으면 바로 에러 처리
        if (!StringUtils.hasText(email)) {
            String redirect = frontRedirectUrl + "?error=no_email";
            response.sendRedirect(redirect);
            return;
        }

        // ==========================
        //   1) 기존 유저면: 바로 JWT 발급
        // ==========================
        User user = userRepository.findByUserEmail(email).orElse(null);

        if (user == null) {
            // ==============================
            //   2) 신규 유저면: state에서 phone/password 가져오기
            // ==============================

            String state = request.getParameter("state");
            String phone = null;
            String rawPassword = null;

            if (StringUtils.hasText(state)) {
                try {
                    String json = new String(Base64.getUrlDecoder().decode(state), StandardCharsets.UTF_8);
                    Map<String, String> m = objectMapper.readValue(json, Map.class);
                    phone = m.get("phone");
                    rawPassword = m.get("password");
                } catch (Exception ignore) {}
            }

            // 👉 엔티티가 phone/pw NOT NULL 이므로, 둘 중 하나라도 없으면 가입 불가
            if (!StringUtils.hasText(phone) || !StringUtils.hasText(rawPassword)) {
                String redirect = frontRedirectUrl
                        + "?error=missing_phone_or_password"
                        + "&email=" + url(email)
                        + "&name=" + url(name);
                response.sendRedirect(redirect);
                return;
            }

            // ✅ 여기서 실제 회원 생성 (엔티티 제약 맞춰서)
            user = registerOAuthUserInternal(name, email, phone, rawPassword);
        }

        // ==========================
        //   3) JWT 발급 & 프론트로 전달
        // ==========================
        String token = jwtTokenProvider.createToken(
                user.getUserId(), user.getUserEmail(), user.getUserRole().name()
        );

        String redirect = frontRedirectUrl + "?token=" + url(token);
        response.sendRedirect(redirect);
    }

    // 🔽 OAuth 신규 유저 생성 로직 (User 엔티티 제약에 맞춤)
    private User registerOAuthUserInternal(String name, String email, String phone, String rawPassword) {
        return userRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .userName(name != null ? name : "USER")
                            .userEmail(email)
                            .userPw(passwordEncoder.encode(rawPassword))
                            .userPhone(phone) // NOT NULL
                            .build();
                    return userRepository.save(newUser);
                });
    }

    private static String url(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
