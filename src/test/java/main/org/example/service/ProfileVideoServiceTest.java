package main.org.example.service;

import org.example.DTO.request.VideoProfileRequest;
import org.example.DTO.response.VideoProfileResponse;
import org.example.config.RabbitMqConfig;
import org.example.config.RestTemplateAuthInterceptor;
import org.example.entity.ProfileVideo;
import org.example.entity.UserProfile;
import org.example.repository.ProfileVideoRepository;
import org.example.repository.SubscriptionRepository;
import org.example.repository.UserProfileRepository;
import org.example.service.ProfileVideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileVideoServiceTest {

    @Mock
    private ProfileVideoRepository profileVideoRepository;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private RabbitMqConfig rabbitMqConfig;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProfileVideoService profileVideoService;

    private final UUID viewerId = UUID.randomUUID();
    private final UUID profileOwnerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getVideosForProfile_ReturnsVideos_WhenProfileIsPublic() {
        UserProfile profile = mock(UserProfile.class);
        when(profile.isPrivate()).thenReturn(false);
        when(userProfileRepository.findById(profileOwnerId)).thenReturn(Optional.of(profile));
        List<ProfileVideo> videos = List.of(
                ProfileVideo.builder().id(UUID.randomUUID()).authorId(profileOwnerId).title("t").description("d").videoUrl("url").build()
        );
        when(profileVideoRepository.findAllByAuthorId(profileOwnerId)).thenReturn(videos);

        List<VideoProfileResponse> result = profileVideoService.getVideosForProfile(viewerId, profileOwnerId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("t");
    }

    @Test
    void getVideosForProfile_ThrowsSecurityException_WhenProfileIsPrivateAndNotSubscribed() {
        UserProfile profile = mock(UserProfile.class);
        when(profile.isPrivate()).thenReturn(true);
        when(userProfileRepository.findById(profileOwnerId)).thenReturn(Optional.of(profile));
        when(subscriptionRepository.existsBySubscriberIdAndTargetUserId(viewerId, profileOwnerId)).thenReturn(false);

        assertThatThrownBy(() -> profileVideoService.getVideosForProfile(viewerId, profileOwnerId))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Profile is private");
    }

    @Test
    void getVideosForProfile_ReturnsVideos_WhenProfileIsPrivateAndViewerIsOwner() {
        UserProfile profile = mock(UserProfile.class);
        when(profile.isPrivate()).thenReturn(true);
        when(userProfileRepository.findById(profileOwnerId)).thenReturn(Optional.of(profile));
        List<ProfileVideo> videos = List.of(
                ProfileVideo.builder().id(UUID.randomUUID()).authorId(profileOwnerId).title("t").description("d").videoUrl("url").build()
        );
        when(profileVideoRepository.findAllByAuthorId(profileOwnerId)).thenReturn(videos);

        List<VideoProfileResponse> result = profileVideoService.getVideosForProfile(profileOwnerId, profileOwnerId);

        assertThat(result).hasSize(1);
    }

    @Test
    void getVideosForProfile_ReturnsVideos_WhenProfileIsPrivateAndViewerIsSubscribed() {
        UserProfile profile = mock(UserProfile.class);
        when(profile.isPrivate()).thenReturn(true);
        when(userProfileRepository.findById(profileOwnerId)).thenReturn(Optional.of(profile));
        when(subscriptionRepository.existsBySubscriberIdAndTargetUserId(viewerId, profileOwnerId)).thenReturn(true);
        List<ProfileVideo> videos = List.of(
                ProfileVideo.builder().id(UUID.randomUUID()).authorId(profileOwnerId).title("t").description("d").videoUrl("url").build()
        );
        when(profileVideoRepository.findAllByAuthorId(profileOwnerId)).thenReturn(videos);

        List<VideoProfileResponse> result = profileVideoService.getVideosForProfile(viewerId, profileOwnerId);

        assertThat(result).hasSize(1);
    }

    @Test
    void getVideosForProfile_ThrowsRuntimeException_WhenProfileNotFound() {
        when(userProfileRepository.findById(profileOwnerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileVideoService.getVideosForProfile(viewerId, profileOwnerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profile not found");
    }

    @Test
    void getVideosWithPrivacy_ReturnsVideos_WhenProfileIsNotPrivate() {
        VideoProfileRequest request = mock(VideoProfileRequest.class);
        when(request.profileOwnerID()).thenReturn(profileOwnerId);
        when(rabbitMqConfig.restTemplate()).thenReturn(restTemplate);
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(false);

        List<ProfileVideo> videos = List.of(
                ProfileVideo.builder().id(UUID.randomUUID()).authorId(profileOwnerId).title("t").description("d").videoUrl("url").build()
        );
        when(profileVideoRepository.findAllByAuthorId(profileOwnerId)).thenReturn(videos);

        List<VideoProfileResponse> result = profileVideoService.getVideosWithPrivacy(request, "jwt");

        assertThat(result).hasSize(1);
    }

    @Test
    void getVideosWithPrivacy_ReturnsEmptyList_WhenIsPrivateIsNull() {
        VideoProfileRequest request = mock(VideoProfileRequest.class);
        when(request.profileOwnerID()).thenReturn(profileOwnerId);
        when(rabbitMqConfig.restTemplate()).thenReturn(restTemplate);
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(null);

        List<VideoProfileResponse> result = profileVideoService.getVideosWithPrivacy(request, "jwt");

        assertThat(result).isEmpty();
    }

    @Test
    void getVideosWithPrivacy_ReturnsVideos_WhenProfileIsPrivateAndViewerIsSubscribed() {
        VideoProfileRequest request = mock(VideoProfileRequest.class);
        when(request.profileOwnerID()).thenReturn(profileOwnerId);
        when(request.viewerId()).thenReturn(viewerId);
        when(rabbitMqConfig.restTemplate()).thenReturn(restTemplate);
        when(restTemplate.getForObject(contains("/is-private"), eq(Boolean.class))).thenReturn(true);
        when(restTemplate.getForObject(contains("/is-subscribed"), eq(Boolean.class))).thenReturn(true);

        List<ProfileVideo> videos = List.of(
                ProfileVideo.builder().id(UUID.randomUUID()).authorId(profileOwnerId).title("t").description("d").videoUrl("url").build()
        );
        when(profileVideoRepository.findAllByAuthorId(profileOwnerId)).thenReturn(videos);

        List<VideoProfileResponse> result = profileVideoService.getVideosWithPrivacy(request, "jwt");

        assertThat(result).hasSize(1);
    }

    @Test
    void getVideosWithPrivacy_ReturnsEmptyList_WhenProfileIsPrivateAndViewerIsNotSubscribed() {
        VideoProfileRequest request = mock(VideoProfileRequest.class);
        when(request.profileOwnerID()).thenReturn(profileOwnerId);
        when(request.viewerId()).thenReturn(viewerId);
        when(rabbitMqConfig.restTemplate()).thenReturn(restTemplate);
        when(restTemplate.getForObject(contains("/is-private"), eq(Boolean.class))).thenReturn(true);
        when(restTemplate.getForObject(contains("/is-subscribed"), eq(Boolean.class))).thenReturn(false);

        List<VideoProfileResponse> result = profileVideoService.getVideosWithPrivacy(request, "jwt");

        assertThat(result).isEmpty();
    }

    @Test
    void getUserIdFromPrincipal_ReturnsUUID_WhenPrincipalNameIsUUID() {
        UUID uuid = UUID.randomUUID();
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(uuid.toString());

        UUID result = profileVideoService.getUserIdFromPrincipal(principal);

        assertThat(result).isEqualTo(uuid);
    }


    @Test
    void getVideosForProfile_ThrowsSecurityException_WhenProfileIsPrivateAndViewerIdIsNull() {
        UserProfile profile = mock(UserProfile.class);
        when(profile.isPrivate()).thenReturn(true);
        when(userProfileRepository.findById(profileOwnerId)).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> profileVideoService.getVideosForProfile(null, profileOwnerId))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Profile is private");
    }

    @Test
    void getVideosForProfile_ReturnsEmptyList_WhenNoVideosExistForProfile() {
        UserProfile profile = mock(UserProfile.class);
        when(profile.isPrivate()).thenReturn(false);
        when(userProfileRepository.findById(profileOwnerId)).thenReturn(Optional.of(profile));
        when(profileVideoRepository.findAllByAuthorId(profileOwnerId)).thenReturn(List.of());

        List<VideoProfileResponse> result = profileVideoService.getVideosForProfile(viewerId, profileOwnerId);

        assertThat(result).isEmpty();
    }

    @Test
    void getVideosWithPrivacy_ThrowsRuntimeException_WhenRestTemplateReturnsError() {
        VideoProfileRequest request = mock(VideoProfileRequest.class);
        when(request.profileOwnerID()).thenReturn(profileOwnerId);
        when(rabbitMqConfig.restTemplate()).thenReturn(restTemplate);
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenThrow(new RuntimeException("Service error"));

        assertThatThrownBy(() -> profileVideoService.getVideosWithPrivacy(request, "jwt"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service error");
    }

    @Test
    void getUserIdFromPrincipal_ThrowsIllegalArgumentException_WhenPrincipalNameIsNotUUID() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("invalid-uuid");

        assertThatThrownBy(() -> profileVideoService.getUserIdFromPrincipal(principal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid UUID string");
    }
}
