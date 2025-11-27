package com.example.piuda.config.oauth;

import com.example.piuda.domain.Entity.User;
import com.example.piuda.User.UserRepository;
import com.example.piuda.User.UserService;
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

import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;      // 🔁 UserService 대신 이걸 사용
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

        User user = userRepository.findByUserEmail(email).orElse(null);
        if (user == null) {
            if (!StringUtils.hasText(phone) || !StringUtils.hasText(rawPassword)) {
                String redirect = frontRedirectUrl +
                        "?error=missing_phone_or_password&email=" + url(email) +
                        "&name=" + url(name);
                response.sendRedirect(redirect);
                return;
            }

            // ✅ 여기서 UserService 대신 직접 회원 생성
            user = registerOAuthUserInternal(name, email, phone, rawPassword);
        }

        String token = jwtTokenProvider.createToken(
                user.getUserId(), user.getUserEmail(), user.getUserRole().name()
        );
        String redirect = frontRedirectUrl + "?token=" + url(token);
        response.sendRedirect(redirect);
    }

    // 🔽 UserService.registerOAuthUser 와 동일한 로직을 이 핸들러 안으로 옮김
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
