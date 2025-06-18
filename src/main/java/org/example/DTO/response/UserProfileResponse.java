package org.example.DTO.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder

public record UserProfileResponse(
        UUID id,
        String username,
        String email,
        String avatar_url,
        String profileDescription,
        String background_url,
        String country,
        String city,
        boolean isPrivate,
        LocalDateTime createdAt
) {}

