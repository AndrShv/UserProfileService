package org.example.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.ProfileVideo;
import org.example.event.VideoCreatingEvent;
import org.example.repository.ProfileVideoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MsgConsumerFromUserProfileVideoService {

    private final ProfileVideoRepository profileVideoRepository;

    @RabbitListener(queues = "video.create.queue")
    public void handleVideoCreated(VideoCreatingEvent event) {
        System.out.println("Получено событие: " + event);
        System.out.println(event.getAuthorId());
        System.out.println(event.getTitle());
        System.out.println(event.getDescription());
        System.out.println(event.getVideoUrl());

        log.info("Received video creation event: {}", event);

        // Проверка уникальности videoUrl
        Optional<ProfileVideo> existing = profileVideoRepository.findByVideoUrl(event.getVideoUrl());
        String finalUrl = event.getVideoUrl();

        if (existing.isPresent()) {
            String newUrl = generateUniqueUrl(event.getVideoUrl());
            log.warn("Video URL already exists. Generated new videoUrl: {}", newUrl);
            finalUrl = newUrl;
        }

        ProfileVideo video = ProfileVideo.builder()
                .id(UUID.randomUUID())
                .authorId(event.getAuthorId())
                .title(event.getTitle())
                .description(event.getDescription())
                .videoUrl(finalUrl)
                .thumbnailUrl(event.getThumbnailUrl())
                .duration(event.getDuration())
                .category(event.getCategory())
                .build();

        try {
            ProfileVideo saved = profileVideoRepository.save(video);
            log.info("✅ Saved profile video: id={}, authorId={}, title={}",
                    saved.getId(), saved.getAuthorId(), saved.getTitle());
            log.info("Successfully saved.");
            System.out.println("ID автора: " + saved.getAuthorId());
            System.out.println("Сохраняю видео в БД...");
        } catch (Exception e) {
            log.error("❌ Failed to save profile video: {}", e.getMessage(), e);
        }
    }

    private String generateUniqueUrl(String originalUrl) {
        int extensionIndex = originalUrl.lastIndexOf(".mp4");
        if (extensionIndex == -1) return originalUrl + "-" + UUID.randomUUID();
        return originalUrl.substring(0, extensionIndex)
                + "-" + UUID.randomUUID()
                + ".mp4";
    }
}
