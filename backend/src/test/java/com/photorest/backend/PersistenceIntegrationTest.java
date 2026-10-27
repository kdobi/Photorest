package com.photorest.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.photorest.backend.folder.Folder;
import com.photorest.backend.folder.FolderRepository;
import com.photorest.backend.photo.Photo;
import com.photorest.backend.photo.PhotoRepository;
import com.photorest.backend.user.User;
import com.photorest.backend.user.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PersistenceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    void savesAndFindsUserFolderAndPhoto() {
        User user = userRepository.saveAndFlush(
                new User(123_456_789L, "테스트 사용자", "https://example.com/profile.jpg")
        );
        Folder folder = folderRepository.saveAndFlush(new Folder(user, "보관함"));
        Photo photo = photoRepository.saveAndFlush(
                new Photo(
                        folder,
                        "users/test-user/photos/test-photo.jpg",
                        "test-photo.jpg",
                        "image/jpeg",
                        1_024L
                )
        );

        assertNotNull(user.getId());
        assertNotNull(folder.getId());
        assertNotNull(photo.getId());
        assertTrue(userRepository.findByKakaoId(123_456_789L).isPresent());
        assertEquals(1, folderRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).size());

        List<Photo> photos = photoRepository.findAllByFolderIdOrderByUploadedAtDesc(folder.getId());
        assertEquals(1, photos.size());
        assertEquals("test-photo.jpg", photos.getFirst().getOriginalFilename());
        assertTrue(photoRepository.findByIdAndFolderUserId(photo.getId(), user.getId()).isPresent());
    }
}
