package org.example.repository;

import org.example.entity.ProfileVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileVideoRepository extends JpaRepository<ProfileVideo, UUID> {
    List<ProfileVideo> findAllByAuthorId(UUID authorId);
    Optional<ProfileVideo> findByVideoUrl(String videoUrl);
}
