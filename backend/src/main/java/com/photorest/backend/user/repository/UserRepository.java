package com.photorest.backend.user.repository;

import com.photorest.backend.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByKakaoId(Long kakaoId);
}
