package com.photorest.backend.auth.service;

import com.photorest.backend.user.service.UserService;
import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.jspecify.annotations.NonNull;

@Service
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public KakaoOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public @NonNull OAuth2User loadUser(@NonNull OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        userService.findOrCreateKakaoUser(
                extractKakaoId(attributes),
                extractProperty(attributes, "nickname"),
                extractProperty(attributes, "profile_image")
        );

        return oAuth2User;
    }

    private Long extractKakaoId(Map<String, Object> attributes) {
        Object id = attributes.get("id");
        if (id instanceof Number number) {
            return number.longValue();
        }
        throw new OAuth2AuthenticationException(
                new OAuth2Error("invalid_kakao_response"),
                "카카오 사용자 번호를 찾을 수 없습니다."
        );
    }

    private String extractProperty(Map<String, Object> attributes, String propertyName) {
        Object properties = attributes.get("properties");
        if (!(properties instanceof Map<?, ?> propertyMap)) {
            return null;
        }
        Object value = propertyMap.get(propertyName);
        return value instanceof String stringValue ? stringValue : null;
    }
}
