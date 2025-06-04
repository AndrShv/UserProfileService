package org.example.service;

import lombok.AllArgsConstructor;
import org.example.DTO.request.UserProfileRequest;
import org.example.DTO.response.UserProfileResponse;
import org.example.entity.Subscription;
import org.example.entity.UserProfile;
import org.example.repository.SubscriptionRepository;
import org.example.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final SubscriptionRepository subscriptionRepository;

    // 1) Создать профиль
    public UserProfileResponse createUserProfile(UserProfileRequest req) {
        if (userProfileRepository.existsByUserTagId(req.userTagId())) {
            throw new IllegalArgumentException("UserTagId already exists");
        }
        if (userProfileRepository.existsByAvatarUrl(req.avatar_url())) {
            throw new IllegalArgumentException("Avatar URL already in use");
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setNickName(req.nickName());
        userProfile.setUserTagId(req.userTagId());
        userProfile.setAvatarUrl(req.avatar_url());
        userProfile.setProfileDescription(req.profileDescription());
        userProfile.setBackground_url(req.background_url());
        userProfile.setCountry(req.country());
        userProfile.setCity(req.city());
        userProfile.setPrivate(req.isPrivate());

        userProfileRepository.save(userProfile);

        return toResponse(userProfile);
    }

    // 2) Получить профиль по userTagId
    public UserProfileResponse getUserProfileByUserTagId(String userTagId) {
        UserProfile userProfile = userProfileRepository.findByUserTagId(userTagId);
        if (userProfile == null) {
            return null;
        }
        return toResponse(userProfile);
    }

    // 3) Получить профиль по никнейму
    public UserProfileResponse getUserProfileByNickName(String nickName) {
        UserProfile userProfile = userProfileRepository.findByNickName(nickName);
        if (userProfile == null) {
            return null;
        }
        return toResponse(userProfile);
    }

    // 4) Получить профиль по ID пользователя (UUID)
    public UserProfileResponse getUserProfileByUserId(UUID userId) {
        UserProfile userProfile = userProfileRepository.findById(userId).orElse(null);
        if (userProfile == null) {
            return null;
        }
        return toResponse(userProfile);
    }

    // 5) Обновить профиль (по userTagId)
    public UserProfileResponse updateUserProfile(String userTagId, UserProfileRequest req) {
        UserProfile userProfile = userProfileRepository.findByUserTagId(userTagId);
        if (userProfile == null) {
            return null;
        }
        // Можно добавить проверки на уникальность fields, если нужно
        userProfile.setNickName(req.nickName());
        userProfile.setAvatarUrl(req.avatar_url());
        userProfile.setProfileDescription(req.profileDescription());
        userProfile.setBackground_url(req.background_url());
        userProfile.setCountry(req.country());
        userProfile.setCity(req.city());
        userProfile.setPrivate(req.isPrivate());

        userProfileRepository.save(userProfile);
        return toResponse(userProfile);
    }

    // 6) Удалить профиль (по userTagId)
    public void deleteUserProfile(String userTagId) {
        UserProfile userProfile = userProfileRepository.findByUserTagId(userTagId);
        if (userProfile != null) {
            userProfileRepository.delete(userProfile);
        } else {
            throw new IllegalArgumentException("User profile not found");
        }
    }

    // ======================= ПОДПИСКИ =======================

    // 7) Подписаться на пользователя
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

    // 8) Отписаться от пользователя
    public void unsubscribeFromUser(UUID subscriberId, UUID targetUserId) {
        subscriptionRepository.deleteBySubscriberIdAndTargetUserId(subscriberId, targetUserId);
    }

    // 9) Получить список подписчиков у данного пользователя
    public List<UUID> getSubscribersForUser(UUID userId) {
        List<Subscription> subs = subscriptionRepository.findAllByTargetUserId(userId);
        return subs.stream()
                .map(Subscription::getSubscriberId)
                .collect(Collectors.toList());
    }

    // 10) Получить список пользователей, на которых подписан данный пользователь
    public List<UUID> getSubscribedUsers(UUID userId) {
        List<Subscription> subs = subscriptionRepository.findAllBySubscriberId(userId);
        return subs.stream()
                .map(Subscription::getTargetUserId)
                .collect(Collectors.toList());
    }

    private UserProfileResponse toResponse(UserProfile up) {
        return new UserProfileResponse(
                up.getId(),
                up.getNickName(),
                up.getUserTagId(),
                up.getAvatarUrl(),
                up.getProfileDescription(),
                up.getBackground_url(),
                up.getCountry(),
                up.getCity(),
                up.isPrivate(),
                up.getCreatedAt()
        );
    }
}
