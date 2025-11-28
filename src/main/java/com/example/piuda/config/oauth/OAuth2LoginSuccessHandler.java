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

        String email = (String) principal.getAttributes().get("email");
        String name  = (String) principal.getAttributes().get("name");

        if (!StringUtils.hasText(email)) {
            String redirect = frontRedirectUrl + "?error=no_email";
            response.sendRedirect(redirect);
            return;
        }

        // ✅ 1) 세션에서 phone/password 가져오기
        var session = request.getSession(false);
        String phone = null;
        String rawPassword = null;
        if (session != null) {
            phone = (String) session.getAttribute("OAUTH_PHONE");
            rawPassword = (String) session.getAttribute("OAUTH_PW");
            // 가져온 뒤 세션에서 지워주면 깔끔
            session.removeAttribute("OAUTH_PHONE");
            session.removeAttribute("OAUTH_PW");
        }

        User user = userRepository.findByUserEmail(email).orElse(null);

        if (user == null) {
            // 🔐 여전히 phone / password 둘 다 필요한 구조라면 이 체크 유지
            if (!StringUtils.hasText(phone) || !StringUtils.hasText(rawPassword)) {
                String redirect = frontRedirectUrl
                        + "?error=missing_phone_or_password"
                        + "&email=" + url(email)
                        + "&name=" + url(name);
                response.sendRedirect(redirect);
                return;
            }

            user = registerOAuthUserInternal(name, email, phone, rawPassword);
        }

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
