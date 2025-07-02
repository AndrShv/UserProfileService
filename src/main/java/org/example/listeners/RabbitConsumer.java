package org.example.listeners;

import org.example.entity.UserProfile;
import org.example.event.UserRegisteredEvent;
import org.example.service.UserProfileService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
@Service
public class RabbitConsumer {

    private final UserProfileService userProfileService;

    public RabbitConsumer(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @RabbitListener(queues = "user.registered.queue")
    public void receiveFromAuth(UserRegisteredEvent event){
        System.out.println("Получено событие регистрации:");
        System.out.println("Email: " + event.getEmail());
        System.out.println("Username: " + event.getUsername());
        System.out.println("ID: " + event.getId());

        boolean exists = userProfileService.existsById(event.getId());
        if (exists) {
            System.out.println("User profile уже существует с ID: " + event.getId() + ", пропускаем создание.");
            return;
        }

        UserProfile profile = new UserProfile();
        profile.setId(event.getId());
        profile.setUsername(event.getUsername());
        profile.setEmail(event.getEmail());
        UserProfile saved = userProfileService.save(profile);
        System.out.println("Saved profile ID: " + saved.getId());

        System.out.println("User profile created: " + event.getUsername() + ";  ID: " +  event.getId());
    }
}
