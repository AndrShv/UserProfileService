package org.example.service;

import lombok.AllArgsConstructor;
import org.example.DTO.request.UserProfileRequest;
import org.example.DTO.response.UserProfileResponse;
import org.example.entity.Subscription;
import org.example.entity.UserProfile;
import org.example.event.UserRegisteredEvent;
import org.example.repository.SubscriptionRepository;
import org.example.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final SubscriptionRepository subscriptionRepository;
    // 1) Создать профиль
    public UserProfileResponse createUserProfile(UserRegisteredEvent userRegisteredEvent) {
        if (userProfileRepository.existsByEmail(userRegisteredEvent.getEmail())) {
            throw new IllegalArgumentException("User profile with this email already exists");
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setId(userRegisteredEvent.getId());
        userProfile.setUsername(userRegisteredEvent.getUsername());
        userProfile.setEmail(userRegisteredEvent.getEmail());
        userProfile.setPrivate(false);
        userProfile.setCreatedAt(LocalDateTime.now());

        userProfile = userProfileRepository.save(userProfile);

        return toResponse(userProfile);
    }

    // 2) Получить профиль по ID (UUID)
    public UserProfileResponse getUserProfileById(UUID id) {
        UserProfile userProfile = userProfileRepository.findById(id).orElse(null);
        if (userProfile == null) {
            return null;
        }
        return toResponse(userProfile);
    }

    // 3) Получить профиль по никнейму
    public UserProfileResponse getUserProfileByUserName(String username) {
        UserProfile userProfile = userProfileRepository.findByUsername(username);
        if (userProfile == null) {
            return null;
        }
        return toResponse(userProfile);
    }

    // 4) Обновить профиль по ID (UUID)
    public UserProfileResponse updateUserProfile(UUID id, UserProfileRequest req) {
        UserProfile userProfile = userProfileRepository.findById(id).orElse(null);
        if (userProfile == null) {
            return null;
        }
        // Можно добавить проверки на уникальность полей, если нужно
        userProfile.setUsername(req.username());
        userProfile.setAvatarUrl(req.avatar_url());
        userProfile.setProfileDescription(req.profileDescription());
        userProfile.setBackground_url(req.background_url());
        userProfile.setCountry(req.country());
        userProfile.setCity(req.city());
        userProfile.setPrivate(req.isPrivate());

        userProfileRepository.save(userProfile);
        return toResponse(userProfile);
    }

    // 5) Удалить профиль по ID (UUID)
    public void deleteUserProfile(UUID id) {
        UserProfile userProfile = userProfileRepository.findById(id).orElse(null);
        if (userProfile != null) {
            userProfileRepository.delete(userProfile);
        } else {
            throw new IllegalArgumentException("User profile not found");
        }
    }

    // ======================= ПОДПИСКИ =======================

    // 6) Подписаться на пользователя
    public void subscribeToUser(UUID subscriberId, UUID targetUserId) {
        if (subscriberId.equals(targetUserId)) {
            throw new IllegalArgumentException("You cannot subscribe to yourself.");
        }
        boolean exists = subscriptionRepository
                .findBySubscriberIdAndTargetUserId(subscriberId, targetUserId)
                .isPresent();

        if (exists) {
            throw new IllegalStateException("Already subscribed to this user.");
        }

        Subscription subscription = new Subscription();
        subscription.setSubscriberId(subscriberId);
        subscription.setTargetUserId(targetUserId);
        subscriptionRepository.save(subscription);
    }

    // 7) Отписаться от пользователя
    public void unsubscribeFromUser(UUID subscriberId, UUID targetUserId) {
        subscriptionRepository.deleteBySubscriberIdAndTargetUserId(subscriberId, targetUserId);
    }

    // 8) Получить список подписчиков у данного пользователя
    public List<UUID> getSubscribersForUser(UUID userId) {
        List<Subscription> subs = subscriptionRepository.findAllByTargetUserId(userId);
        return subs.stream()
                .map(Subscription::getSubscriberId)
                .collect(Collectors.toList());
    }

    // 9) Получить список пользователей, на которых подписан данный пользователь
    public List<UUID> getSubscribedUsers(UUID userId) {
        List<Subscription> subs = subscriptionRepository.findAllBySubscriberId(userId);
        return subs.stream()
                .map(Subscription::getTargetUserId)
                .collect(Collectors.toList());
    }

    private UserProfileResponse toResponse(UserProfile userProfile) {
        return UserProfileResponse.builder()
                .id(userProfile.getId())
                .username(userProfile.getUsername())
                .email(userProfile.getEmail())
                .avatar_url(userProfile.getAvatarUrl())
                .profileDescription(userProfile.getProfileDescription())
                .background_url(userProfile.getBackground_url())
                .country(userProfile.getCountry())
                .city(userProfile.getCity())
                .isPrivate(userProfile.isPrivate())
                .createdAt(userProfile.getCreatedAt())
                .build();
    }

    public UserProfile findByUsername(String username) {
        return userProfileRepository.findByUsername(username);
    }

    public UserProfile save(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }
    public boolean existsById(UUID id) {
        return userProfileRepository.existsById(id);
    }

}
