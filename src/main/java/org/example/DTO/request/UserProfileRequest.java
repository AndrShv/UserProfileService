package org.example.DTO.request;

public record UserProfileRequest(
        String nickName,
        String userTagId,
        String avatar_url,
        String profileDescription,
        String background_url,
        String country,
        String city,
        boolean isPrivate
) {}
