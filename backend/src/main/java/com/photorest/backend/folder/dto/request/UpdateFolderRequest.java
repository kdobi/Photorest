package com.photorest.backend.folder.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 폴더 이름 변경 요청입니다. */
public record UpdateFolderRequest(
        @NotBlank(message = "폴더 이름은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "폴더 이름은 100자 이하여야 합니다.")
        String name
) {

    public String normalizedName() {
        return name.trim();
    }
}
