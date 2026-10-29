package com.photorest.backend.folder.service;

import com.photorest.backend.folder.domain.Folder;
import com.photorest.backend.folder.repository.FolderRepository;
import com.photorest.backend.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class FolderService {

    public static final String DEFAULT_FOLDER_NAME = "보관함";

    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public Folder createDefaultFolder(User user) {
        return folderRepository.save(new Folder(user, DEFAULT_FOLDER_NAME));
    }

    @Transactional
    public Folder createFolder(User user, String name) {
        if (folderRepository.existsByUserIdAndName(user.getId(), name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "같은 이름의 폴더가 이미 있습니다.");
        }

        return folderRepository.save(new Folder(user, name));
    }
}
