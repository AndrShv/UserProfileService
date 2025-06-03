package org.example.DTO.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String nickName,
        String userTagId,
        String avatar_url,
        String profileDescription,
        String background_url,
        String country,
        String city,
        boolean isPrivate,
        LocalDateTime createdAt
) {}

