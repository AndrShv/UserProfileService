package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.DTO.request.UserProfileRequest;
import org.example.DTO.response.UserProfileResponse;
import org.example.entity.UserProfile;
import org.example.repository.UserProfileRepository;
import org.example.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;
    private final UserProfileRepository userProfileRepository;


    // Получение по UUID id
    @GetMapping("/user-profiles/{id}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // некорректный формат UUID
        }
        return userProfileRepository.findById(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Обновление по UUID id
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UserProfileRequest req) {
        UserProfileResponse resp = service.updateUserProfile(id, req);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    // Удаление по UUID id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteUserProfile(id);
        return ResponseEntity.noContent().build();
    }

    // Подписка
    @PostMapping("/{subscriberId}/subscribe/{targetId}")
    public ResponseEntity<Void> subscribe(
            @PathVariable UUID subscriberId,
            @PathVariable UUID targetId) {
        service.subscribeToUser(subscriberId, targetId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{subscriberId}/unsubscribe/{targetId}")
    public ResponseEntity<Void> unsubscribe(
            @PathVariable UUID subscriberId,
            @PathVariable UUID targetId) {
        service.unsubscribeFromUser(subscriberId, targetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/subscribers")
    public ResponseEntity<List<UUID>> getSubscribers(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getSubscribersForUser(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UUID>> getFollowing(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getSubscribedUsers(userId));
    }
}
