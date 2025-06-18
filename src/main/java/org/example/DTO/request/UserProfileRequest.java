package org.example.DTO.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserProfileRequest(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String avatar_url,
        String profileDescription,
        String background_url,
        String country,
        String city,
        boolean isPrivate
) {}
