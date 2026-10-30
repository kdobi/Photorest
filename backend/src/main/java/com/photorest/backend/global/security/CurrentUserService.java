package com.photorest.backend.global.security;

import com.photorest.backend.user.domain.User;
import com.photorest.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** 카카오 로그인 세션의 사용자 정보를 우리 서비스의 User로 변환합니다. */
@Service
public class CurrentUserService {

    private final UserService userService;

    public CurrentUserService(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser(OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Object id = oAuth2User.getAttributes().get("id");
        if (!(id instanceof Number number)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "카카오 사용자 정보를 찾을 수 없습니다.");
        }

        return userService.findByKakaoId(number.longValue())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "등록된 사용자를 찾을 수 없습니다."
                ));
    }
}
