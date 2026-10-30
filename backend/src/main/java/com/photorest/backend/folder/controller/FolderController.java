package com.photorest.backend.folder.controller;

import com.photorest.backend.folder.domain.Folder;
import com.photorest.backend.folder.dto.request.CreateFolderRequest;
import com.photorest.backend.folder.dto.request.UpdateFolderRequest;
import com.photorest.backend.folder.dto.response.FolderResponse;
import com.photorest.backend.folder.service.FolderService;
import com.photorest.backend.global.security.CurrentUserService;
import com.photorest.backend.user.domain.User;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;
    private final CurrentUserService currentUserService;

    public FolderController(FolderService folderService, CurrentUserService currentUserService) {
        this.folderService = folderService;
        this.currentUserService = currentUserService;
    }

    // 폴더 생성 요청
    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @Valid @RequestBody CreateFolderRequest request
    ) {
        User currentUser = currentUserService.getCurrentUser(oAuth2User);
        Folder folder = folderService.createFolder(currentUser, request.normalizedName());

        return ResponseEntity.status(HttpStatus.CREATED).body(FolderResponse.from(folder));
    }

    // 내 폴더 목록 변환
    @GetMapping
    public List<FolderResponse> getFolders(
            @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return folderService.getFolders(currentUserService.getCurrentUser(oAuth2User)).stream()
                .map(FolderResponse::from)
                .toList();
    }

    // 폴더 이름 변경
    @PatchMapping("/{folderId}")
    public FolderResponse updateFolder(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @PathVariable UUID folderId,
            @Valid @RequestBody UpdateFolderRequest request
    ) {
        User currentUser = currentUserService.getCurrentUser(oAuth2User);
        Folder folder = folderService.updateFolder(currentUser, folderId, request.normalizedName());

        return FolderResponse.from(folder);
    }

    // 폴더 삭제 -> 폴더에 사진 없어야
    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @PathVariable UUID folderId
    ) {
        User currentUser = currentUserService.getCurrentUser(oAuth2User);
        folderService.deleteFolder(currentUser, folderId);

        return ResponseEntity.noContent().build();
    }
}
