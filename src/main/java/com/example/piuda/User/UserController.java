package com.example.piuda.User;

import com.example.piuda.domain.DTO.UserDTO;
import com.example.piuda.User.UserService;
import com.example.piuda.User.UserService.LoginRequest;
import com.example.piuda.User.UserService.TokenResponse;
import com.example.piuda.User.UserService.MeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        if (authentication == null || !authentication.isAuthenticated()) {
            // 이 엔드포인트는 "내 정보" 조회니까,
            // 로그인 안 했으면 401(Unauthorized) 주는 게 자연스러움
            return ResponseEntity.status(401).build();
        }

        // authentication.getName() 에 이메일을 넣도록 설정할 것 (JwtTokenProvider 참조)
        return ResponseEntity.ok(userService.me(authentication.getName()));
    }

    /**
     * ✅ 카카오 로그인 시작 엔드포인트
     * 프론트에서 phone/password를 보내면,
     * 백엔드가 state(JSON → Base64URL)를 만들어서 카카오 인가 URL로 리다이렉트.
     *
     * 예)
     * GET /api/auth/kakao-login?phone=01012341234&password=abcd1234
     */
    @GetMapping("/kakao-login")
    public void kakaoLogin(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String password,
            HttpServletResponse response
    ) throws IOException {

        // state에 넣을 데이터 구성
        Map<String, String> stateMap = new HashMap<>();
        if (phone != null) stateMap.put("phone", phone);
        if (password != null) stateMap.put("password", password);

        String stateJson = objectMapper.writeValueAsString(stateMap);
        String stateBase64 = Base64.getUrlEncoder()
                .encodeToString(stateJson.getBytes(StandardCharsets.UTF_8));

        // Spring Security OAuth2 기본 엔드포인트: /oauth2/authorization/kakao
        String redirectUrl = "/oauth2/authorization/kakao?state=" +
                URLEncoder.encode(stateBase64, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}
