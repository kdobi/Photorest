package com.photorest.backend.folder.controller;

import com.photorest.backend.folder.domain.Folder;
import com.photorest.backend.folder.dto.request.CreateFolderRequest;
import com.photorest.backend.folder.dto.response.FolderResponse;
import com.photorest.backend.folder.service.FolderService;
import com.photorest.backend.user.domain.User;
import com.photorest.backend.user.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;
    private final UserService userService;

    public FolderController(FolderService folderService, UserService userService) {
        this.folderService = folderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @Valid @RequestBody CreateFolderRequest request
    ) {
        User currentUser = findCurrentUser(oAuth2User);
        Folder folder = folderService.createFolder(currentUser, request.normalizedName());

        return ResponseEntity.status(HttpStatus.CREATED).body(FolderResponse.from(folder));
    }

    private User findCurrentUser(OAuth2User oAuth2User) {
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
