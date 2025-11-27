package com.example.piuda.config;

import com.example.piuda.config.JwtAuthenticationFilter;
import com.example.piuda.config.JwtTokenProvider;
import com.example.piuda.config.oauth.CustomOAuth2UserService;
import com.example.piuda.config.oauth.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // JWT 사용하니까 CSRF, 세션 비활성화
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 기본 로그인 방지 (formLogin, httpBasic)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 요청별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 회원가입, 일반 로그인 API는 열어둘지 말지 선택 (지금은 열어둔 상태)
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup", "/api/auth/login").permitAll()

                        // OAuth2 인가/콜백 관련 엔드포인트 허용
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // 정적 리소스/헬스체크 등 필요 시 허용
                        .requestMatchers(
                                "/", "/index.html",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()

                        // 그 외 나머지 API는 인증 필요
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정 (카카오)
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                // 매 요청마다 JWT 인증 필터 태우기
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // 프론트 개발 서버 / 배포 서버 출처 등록 (슬래시 X)
        cfg.setAllowedOrigins(List.of(
                "http://localhost:8080",
                "http://localhost:8081",
                "https://piuda-front.vercel.app",
                "https://piuda-front-git-dev-minkyeong-chois-projects.vercel.app"
        ));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
