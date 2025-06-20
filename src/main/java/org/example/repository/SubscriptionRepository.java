package org.example.repository;

import org.example.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findBySubscriberIdAndTargetUserId(UUID subscriberId, UUID targetUserId);
    List<Subscription> findAllBySubscriberId(UUID subscriberId);
    List<Subscription> findAllByTargetUserId(UUID targetUserId);
    void deleteBySubscriberIdAndTargetUserId(UUID subscriberId, UUID targetUserId);
    boolean existsBySubscriberIdAndTargetUserId(UUID subscriberId, UUID targetUserId);

}

