package com.photorest.backend.user.service;

import com.photorest.backend.folder.service.FolderService;
import com.photorest.backend.user.domain.User;
import com.photorest.backend.user.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final String DEFAULT_DISPLAY_NAME = "카카오 사용자";

    private final UserRepository userRepository;
    private final FolderService folderService;

    public UserService(UserRepository userRepository, FolderService folderService) {
        this.userRepository = userRepository;
        this.folderService = folderService;
    }

    @Transactional
    public User findOrCreateKakaoUser(Long kakaoId, String displayName, String profileImageUrl) {
        return userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> createKakaoUser(kakaoId, displayName, profileImageUrl));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByKakaoId(Long kakaoId) {
        return userRepository.findByKakaoId(kakaoId);
    }

    private User createKakaoUser(Long kakaoId, String displayName, String profileImageUrl) {
        User user = userRepository.save(
                new User(kakaoId, normalizeDisplayName(displayName), profileImageUrl)
        );
        folderService.createDefaultFolder(user);
        return user;
    }

    private String normalizeDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return DEFAULT_DISPLAY_NAME;
        }
        return displayName;
    }
}
