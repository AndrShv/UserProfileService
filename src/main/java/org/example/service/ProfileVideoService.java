package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.DTO.request.VideoProfileRequest;
import org.example.DTO.response.VideoProfileResponse;
import org.example.config.RabbitMqConfig;
import org.example.config.RestTemplateAuthInterceptor;
import org.example.entity.ProfileVideo;
import org.example.entity.UserProfile;
import org.example.repository.ProfileVideoRepository;
import org.example.repository.SubscriptionRepository;
import org.example.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileVideoService {

    private final ProfileVideoRepository profileVideoRepository;
    private final UserProfileRepository userProfileRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final RabbitMqConfig rabbitMqConfig;



    public List<VideoProfileResponse> getVideosForProfile(UUID viewerId, UUID profileOwnerId) {
        UserProfile profile = userProfileRepository.findById(profileOwnerId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        boolean isSubscribed = subscriptionRepository.existsBySubscriberIdAndTargetUserId(viewerId, profileOwnerId);

        if (profile.isPrivate() && viewerId != profileOwnerId && !isSubscribed) {
            throw new SecurityException("Profile is private");
        }

        List<ProfileVideo> videos = profileVideoRepository.findAllByAuthorId(profileOwnerId);
        return videos.stream().map(video -> VideoProfileResponse.builder()
                .id(video.getId())
                .authorId(video.getAuthorId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(video.getVideoUrl())
                .build()).toList();
    }

    public List<VideoProfileResponse> getVideosWithPrivacy(VideoProfileRequest request, String jwtToken) {
        RestTemplate restTemplate = rabbitMqConfig.restTemplate();
        restTemplate.getInterceptors().add(new RestTemplateAuthInterceptor(jwtToken));

        Boolean isPrivate = restTemplate.getForObject(
                "http://localhost:8002/api/profile/" + request.profileOwnerID() + "/is-private",
                Boolean.class
        );

        if (isPrivate == null) return List.of();
        if (!isPrivate) {
            return fetchVideos(request.profileOwnerID());
        }

        Boolean isSubscribed = restTemplate.getForObject(
                "http://subscription-service/api/subscriptions/is-subscribed?fromUserId=" +
                        request.viewerId() + "&toUserId=" + request.profileOwnerID(),
                Boolean.class
        );

        if (Boolean.TRUE.equals(isSubscribed)) {
            return fetchVideos(request.profileOwnerID());
        }

        return List.of();
    }


    private List<VideoProfileResponse> fetchVideos(UUID profileOwnerId) {
        List<ProfileVideo> videos = profileVideoRepository.findAllByAuthorId(profileOwnerId);
        return videos.stream().map(video -> VideoProfileResponse.builder()
                .id(video.getId())
                .authorId(video.getAuthorId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(video.getVideoUrl())
                .build()).toList();
    }

    public UUID getUserIdFromPrincipal(Principal principal) {
        return UUID.fromString(principal.getName());
    }
}
