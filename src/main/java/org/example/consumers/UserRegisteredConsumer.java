package org.example.consumers;

import lombok.RequiredArgsConstructor;
import org.example.DTO.UserRegisteredEvent;
import org.example.entity.UserProfile;
import org.example.repository.UserProfileRepository;
import org.example.topics.KafkaTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredConsumer.class);
    private final UserProfileRepository userProfileRepository;

    @KafkaListener(
            topics = KafkaTopic.USER_REGISTERED,
            groupId = "user-profile-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent: {}", event);

        UserProfile profile = new UserProfile();
        profile.setNickName(event.nickName());
        profile.setUserTagId(event.userTagId());
        profile.setCreatedAt(event.registeredAt().atZone(ZoneId.systemDefault()).toLocalDateTime());

        userProfileRepository.save(profile);
        log.info("UserProfile created and saved for userId={}", event.userId());
    }
}
