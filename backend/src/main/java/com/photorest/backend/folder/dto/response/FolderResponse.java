package com.photorest.backend.folder.dto.response;

import com.photorest.backend.folder.domain.Folder;
import java.time.Instant;
import java.util.UUID;

/** 폴더 조회·생성 결과입니다. */
public record FolderResponse(
        UUID id,
        String name,
        Instant createdAt
) {

    public static FolderResponse from(Folder folder) {
        return new FolderResponse(folder.getId(), folder.getName(), folder.getCreatedAt());
    }
}
