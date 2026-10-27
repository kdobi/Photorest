package com.photorest.backend.folder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

    List<Folder> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Folder> findByIdAndUserId(UUID folderId, UUID userId);

    boolean existsByUserIdAndName(UUID userId, String name);
}
