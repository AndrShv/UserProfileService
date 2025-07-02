package main.org.example.service;

import org.example.DTO.request.VideoProfileRequest;
import org.example.DTO.response.VideoProfileResponse;
import org.example.config.RabbitMqConfig;
import org.example.service.ProfileVideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private RabbitMqConfig rabbitMqConfig;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProfileVideoService profileVideoService;

    private UUID viewerId;
    private UUID profileOwnerId;

    @BeforeEach
    void setUp() {
        viewerId = UUID.randomUUID();
        profileOwnerId = UUID.randomUUID();
    }

    @Test
    void getVideosForProfile_ThrowsIllegalArgumentException_WhenProfileOwnerIdIsNull() {
        assertThatThrownBy(() -> profileVideoService.getVideosForProfile(viewerId, null))
                .isInstanceOf(NullPointerException.class); // або IllegalArgumentException, якщо ти перевіряєш явно
    }

    @Test
    void getVideosWithPrivacy_ReturnsEmptyList_WhenViewerIdIsNullAndProfileIsPrivate() {
        VideoProfileRequest request = new VideoProfileRequest(null, profileOwnerId);
        when(rabbitMqConfig.restTemplate()).thenReturn(restTemplate);
        when(restTemplate.getForObject(contains("/is-private"), eq(Boolean.class))).thenReturn(true);
        when(restTemplate.getForObject(contains("/is-subscribed"), eq(Boolean.class))).thenReturn(false);

        List<VideoProfileResponse> result = profileVideoService.getVideosWithPrivacy(request, "jwt");

        assertThat(result).isEmpty();
    }

    @Test
    void getVideosWithPrivacy_ReturnsEmptyList_WhenProfileOwnerIdIsNull() {
        VideoProfileRequest request = new VideoProfileRequest(viewerId, null);
        when(rabbitMqConfig.restTemplate()).thenReturn(restTemplate);

        List<VideoProfileResponse> result = profileVideoService.getVideosWithPrivacy(request, "jwt");

        assertThat(result).isEmpty();
    }

    @Test
    void getUserIdFromPrincipal_ThrowsNullPointerException_WhenPrincipalIsNull() {
        assertThatThrownBy(() -> profileVideoService.getUserIdFromPrincipal(null))
                .isInstanceOf(NullPointerException.class);
    }
}
