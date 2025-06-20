package org.example.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


public record VideoProfileRequest(
        UUID profileOwnerID,
        UUID viewerId

) {
}
