package com.example.piuda.config.oauth;

import com.example.piuda.domain.Entity.User;
import com.example.piuda.User.UserRepository;
import com.example.piuda.config.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;   // ✅ 랜덤 패스워드용

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;      // ✅ UserService 안 씀 → 순환 의존 X
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.oauth2.front-redirect-url}")
    private String frontRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();

        String email = (String) principal.getAttributes().get("email");
        String name  = (String) principal.getAttributes().get("name");

        // state로 phone/password를 받을 수도 있고, 아닐 수도 있음
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

        // ==========================
        //   신규 사용자 자동 가입 B안
        // ==========================
        User user = userRepository.findByUserEmail(email).orElse(null);

        if (user == null) {
            // phone은 없어도 됨 → null 로 저장
            if (!StringUtils.hasText(phone)) {
                phone = null;
            }

            // 비밀번호 없으면 랜덤으로 생성해서 저장
            if (!StringUtils.hasText(rawPassword)) {
                rawPassword = UUID.randomUUID().toString();
            }

            user = registerOAuthUserInternal(name, email, phone, rawPassword);
        }

        // JWT 발급
        String token = jwtTokenProvider.createToken(
                user.getUserId(), user.getUserEmail(), user.getUserRole().name()
        );

        String redirect = frontRedirectUrl + "?token=" + url(token);
        response.sendRedirect(redirect);
    }

    // OAuth 전용 내부 회원 생성 로직
    private User registerOAuthUserInternal(String name, String email, String phone, String rawPassword) {
        return userRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .userName(name != null ? name : "USER")
                            .userEmail(email)
                            .userPw(passwordEncoder.encode(rawPassword))
                            .userPhone(phone)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    private static String url(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
