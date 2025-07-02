package org.example.service;


import lombok.RequiredArgsConstructor;
import org.example.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public boolean isSubscribed(UUID viewerId, UUID profileOwnerId) {
        if (viewerId == null || profileOwnerId == null) {
            throw new IllegalArgumentException("Viewer ID or Profile Owner ID must not be null");
        }
        return subscriptionRepository.existsBySubscriberIdAndTargetUserId(viewerId, profileOwnerId);
    }
}
