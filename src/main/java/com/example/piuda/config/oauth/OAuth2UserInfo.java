package com.example.piuda.config.oauth;

import lombok.*;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class OAuth2UserInfo {
    private final String provider; // google, kakao, naver
    private final String providerId;
    private final String email;
    private final String name;
    private final Map<String, Object> attributes;

    public static OAuth2UserInfo from(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google" -> {
                return OAuth2UserInfo.builder()
                        .provider("google")
                        .providerId((String) attributes.get("sub"))
                        .email((String) attributes.get("email"))
                        .name((String) attributes.get("name"))
                        .attributes(attributes)
                        .build();
            }
            case "kakao" -> {
                // kakao: { id, kakao_account:{email,...}, properties:{nickname,...}}
                var account = (Map<String, Object>) attributes.get("kakao_account");
                var props = (Map<String, Object>) attributes.get("properties");
                String email = account != null ? (String) account.get("email") : null;
                String nickname = props != null ? (String) props.get("nickname") : null;
                return OAuth2UserInfo.builder()
                        .provider("kakao")
                        .providerId(String.valueOf(attributes.get("id")))
                        .email(email)
                        .name(nickname)
                        .attributes(attributes)
                        .build();
            }
            case "naver" -> {
                // naver: { resultcode, message, response:{id, email, name, ...} }
                var resp = (Map<String, Object>) attributes.get("response");
                return OAuth2UserInfo.builder()
                        .provider("naver")
                        .providerId((String) resp.get("id"))
                        .email((String) resp.get("email"))
                        .name((String) resp.get("name"))
                        .attributes(attributes)
                        .build();
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
}
