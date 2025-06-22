package org.example.controller;

import jakarta.websocket.server.ServerEndpoint;
import lombok.RequiredArgsConstructor;
import org.example.DTO.request.VideoProfileRequest;
import org.example.DTO.response.VideoProfileResponse;
import org.example.service.ProfileVideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos-from-profile")
@RequiredArgsConstructor
public class ProfileVideoController {
    private final ProfileVideoService profileVideoService;

    @GetMapping("/user/{profileOwnerId}")
    public ResponseEntity<List<VideoProfileResponse>> getVideosByUser(@PathVariable UUID profileOwnerId,
                                                                      Principal principal) {
        UUID viewerId;
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            viewerId = profileVideoService.getUserIdFromPrincipal(principal);
        }

        try {
            List<VideoProfileResponse> videos = profileVideoService.getVideosForProfile(viewerId, profileOwnerId);
            return ResponseEntity.ok(videos);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


}
