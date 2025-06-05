package org.example.DTO;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String nickName,
        String userTagId,
        Instant registeredAt
) {}

