package org.example.repository;

import org.example.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    UserProfile findByUsername(String username);
    Optional<UserProfile> findById(UUID id);
    boolean existsByAvatarUrl(String avatarUrl);
    UserProfile findByEmail(String email);
    boolean existsByEmail(String email);
}
