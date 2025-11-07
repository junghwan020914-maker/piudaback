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

        String registrationId = req.getClientRegistration().getRegistrationId(); // google/kakao/naver
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 여기서는 표준화만 하고, 가입/로그인은 SuccessHandler에서 처리
        OAuth2UserInfo info = OAuth2UserInfo.from(registrationId, attributes);

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                Map.of(
                        "provider", info.getProvider(),
                        "providerId", info.getProviderId(),
                        "email", info.getEmail(),
                        "name", info.getName()
                ),
                "email" // getName() 호출 시 사용될 key
        );
    }
}
