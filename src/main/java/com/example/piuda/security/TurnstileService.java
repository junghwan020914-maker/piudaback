package com.example.piuda.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TurnstileService {

    @Value("${turnstile.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TURNSTILE_VERIFY_URL =
            "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    /**
     * Turnstile 토큰 검증
     */
    public boolean verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("Turnstile token is null or empty");
            return false;
        }

        try {
            // 요청 데이터 구성
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("secret", secretKey);
            formData.add("response", token);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request =
                    new HttpEntity<>(formData, headers);

            // Cloudflare API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(
                    TURNSTILE_VERIFY_URL,
                    request,
                    String.class
            );

            // 응답 파싱
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            boolean success = jsonNode.get("success").asBoolean();

            if (success) {
                log.info("Turnstile verification successful");
            } else {
                log.warn("Turnstile verification failed: {}",
                        jsonNode.get("error-codes"));
            }

            return success;

        } catch (Exception e) {
            log.error("Error verifying Turnstile token", e);
            return false;
        }
    }
}