package com.example.piuda.config.oauth;

import com.example.piuda.domain.Entity.User;
import com.example.piuda.User.UserRepository;
import com.example.piuda.User.UserService;
import com.example.piuda.config.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
    private final UserService userService;          // ✅ 추가
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

        // (중요) phone/password는 프런트가 state에 base64(JSON)로 실어 보냄
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

        // 존재하면 로그인, 없으면 즉시 가입
        User user = userRepository.findByUserEmail(email).orElse(null);
        if (user == null) {
            if (!StringUtils.hasText(phone) || !StringUtils.hasText(rawPassword)) {
                String redirect = frontRedirectUrl +
                        "?error=missing_phone_or_password&email=" + url(email) +
                        "&name=" + url(name);
                response.sendRedirect(redirect);
                return;
            }

            // ✅ 여기서 UserService를 통해 회원 생성 (비번 인코딩 포함)
            user = userService.registerOAuthUser(name, email, phone, rawPassword);
        }

        // JWT 발급
        String token = jwtTokenProvider.createToken(
                user.getUserId(), user.getUserEmail(), user.getUserRole().name()
        );
        String redirect = frontRedirectUrl + "?token=" + url(token);
        response.sendRedirect(redirect);
    }

    private static String url(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
