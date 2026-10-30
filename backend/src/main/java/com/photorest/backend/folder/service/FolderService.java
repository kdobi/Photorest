package com.photorest.backend.folder.service;

import com.photorest.backend.folder.domain.Folder;
import com.photorest.backend.folder.repository.FolderRepository;
import com.photorest.backend.photo.repository.PhotoRepository;
import com.photorest.backend.user.domain.User;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FolderService {

    public static final String DEFAULT_FOLDER_NAME = "보관함";

    private final FolderRepository folderRepository;
    private final PhotoRepository photoRepository;

    public FolderService(FolderRepository folderRepository, PhotoRepository photoRepository) {
        this.folderRepository = folderRepository;
        this.photoRepository = photoRepository;
    }

    // 기본 폴더 생성
    public Folder createDefaultFolder(User user) {
        return folderRepository.save(new Folder(user, DEFAULT_FOLDER_NAME));
    }

    // 폴더 생성
    @Transactional
    public Folder createFolder(User user, String name) {
        if (folderRepository.existsByUserIdAndName(user.getId(), name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "같은 이름의 폴더가 이미 있습니다.");
        }

        return folderRepository.save(new Folder(user, name));
    }

    // 폴더 전체 조회
    @Transactional(readOnly = true)
    public List<Folder> getFolders(User user) {
        return folderRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
    }

    // 폴더 이름 업데이트 -> 존재하면 오류
    @Transactional
    public Folder updateFolder(User user, UUID folderId, String name) {
        Folder folder = getOwnedFolder(user, folderId);
        if (!folder.getName().equals(name) && folderRepository.existsByUserIdAndName(user.getId(), name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "같은 이름의 폴더가 이미 있습니다.");
        }

        folder.changeName(name);
        return folder;
    }

    // 폴더 삭제 -> 비어있는 폴더만
    @Transactional
    public void deleteFolder(User user, UUID folderId) {
        Folder folder = getOwnedFolder(user, folderId);
        if (photoRepository.existsByFolderId(folderId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "사진이 있는 폴더는 삭제할 수 없습니다."
            );
        }

        folderRepository.delete(folder);
    }

    // 선택한 폴더 확인 - 진짜 사용자 본인것인가? (보안)
    @Transactional(readOnly = true)
    public Folder getOwnedFolder(User user, UUID folderId) {
        return folderRepository.findByIdAndUserId(folderId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "폴더를 찾을 수 없습니다."
                ));
    }
}
