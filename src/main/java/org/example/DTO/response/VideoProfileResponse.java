package org.example.DTO.response;



import lombok.Builder;

import java.util.UUID;


@Builder
public record VideoProfileResponse(
         UUID id,
         UUID authorId,
         String title,
         String description,
         String videoUrl){
}
