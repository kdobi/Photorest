package com.photorest.backend.photo.repository;

import com.photorest.backend.photo.domain.Photo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

    List<Photo> findAllByFolderIdOrderByUploadedAtDesc(UUID folderId);

    Optional<Photo> findByIdAndFolderUserId(UUID photoId, UUID userId);

    boolean existsByFolderId(UUID folderId);
}
