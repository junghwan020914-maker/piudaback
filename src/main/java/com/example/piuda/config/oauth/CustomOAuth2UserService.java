package com.example.piuda.config.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(req);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // kakao: { id, kakao_account:{email,...}, properties:{nickname,...}}
        var account = (Map<String, Object>) attributes.get("kakao_account");
        var props = (Map<String, Object>) attributes.get("properties");
        String email = account != null ? (String) account.get("email") : null;
        String nickname = props != null ? (String) props.get("nickname") : null;

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                Map.of(
                        "provider", "kakao",
                        "providerId", String.valueOf(attributes.get("id")),
                        "email", email,
                        "name", nickname
                ),
                "email"
        );
    }
}
