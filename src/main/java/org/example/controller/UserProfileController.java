package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.DTO.request.UserProfileRequest;
import org.example.DTO.response.UserProfileResponse;
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

    @PostMapping
    public ResponseEntity<UserProfileResponse> create(@RequestBody @Valid UserProfileRequest req) {
        UserProfileResponse resp = service.createUserProfile(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{tag}")
    public ResponseEntity<UserProfileResponse> getByTag(@PathVariable String tag) {
        UserProfileResponse resp = service.getUserProfileByUserTagId(tag);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{tag}")
    public ResponseEntity<UserProfileResponse> update(
            @PathVariable String tag,
            @RequestBody @Valid UserProfileRequest req) {
        UserProfileResponse resp = service.updateUserProfile(tag, req);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{tag}")
    public ResponseEntity<Void> delete(@PathVariable String tag) {
        service.deleteUserProfile(tag);
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

