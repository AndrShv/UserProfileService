package main.org.example.service;


import java.util.Optional;
import java.util.UUID;
import org.example.DTO.request.UserProfileRequest;
import org.example.DTO.response.UserProfileResponse;
import org.example.entity.Subscription;
import org.example.entity.UserProfile;
import org.example.repository.SubscriptionRepository;
import org.example.repository.UserProfileRepository;
import org.example.service.UserProfileService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserProfileServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    /**
     * Test that an IllegalArgumentException is thrown when attempting to create a user profile
     * with an avatar URL that is already in use by another user.
     */
    @Test
    public void testCreateUserProfile_DuplicateAvatarUrl() {
        UserProfileRequest request = new UserProfileRequest(
                "testNick", "newUserTagId", "http://existing.avatar.url", "description",
                "http://background.url", "Country", "City", false
        );

        when(userProfileRepository.existsByUserTagId("newUserTagId")).thenReturn(false);
        when(userProfileRepository.existsByAvatarUrl("http://existing.avatar.url")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userProfileService.createUserProfile(request));
    }

    /**
     * Test that an IllegalArgumentException is thrown when attempting to create a user profile
     * with a userTagId that already exists in the system.
     */
    @Test
    public void testCreateUserProfile_DuplicateUserTagId() {
        UserProfileRequest request = new UserProfileRequest(
                "testNick", "existingUserTagId", "http://avatar.url", "description",
                "http://background.url", "Country", "City", false
        );

        when(userProfileRepository.existsByUserTagId("existingUserTagId")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userProfileService.createUserProfile(request));
    }

    /**
     * Tests the deleteUserProfile method when the user profile is not found.
     * This test verifies that an IllegalArgumentException is thrown when attempting to delete a non-existent user profile.
     */
    @Test
    public void testDeleteUserProfile_UserProfileNotFound() {
        MockitoAnnotations.openMocks(this);

        String nonExistentUserTagId = "nonexistent123";
        when(userProfileRepository.findByUserTagId(nonExistentUserTagId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.deleteUserProfile(nonExistentUserTagId);
        });
    }

    /**
     * Test case for updateUserProfile method when the user profile is not found.
     * This test verifies that the method returns null when the userProfile is null.
     */
    @Test
    public void testUpdateUserProfileWhenUserNotFound() {
        String userTagId = "nonexistent_user";
        UserProfileRequest req = new UserProfileRequest(
                "newNickName", userTagId, "newAvatar.jpg", "New description",
                "newBackground.jpg", "NewCountry", "NewCity", false
        );

        when(userProfileRepository.findByUserTagId(userTagId)).thenReturn(null);

        UserProfileResponse result = userProfileService.updateUserProfile(userTagId, req);

        assertNull(result);
    }

    /**
     * Test case for createUserProfile method when the userTagId does not exist but the avatar_url is already in use.
     * This test verifies that an IllegalArgumentException is thrown when attempting to create a user profile
     * with a unique userTagId but an avatar_url that is already associated with another user profile.
     */
    @Test
    public void test_createUserProfile_AvatarUrlAlreadyInUse() {
        UserProfileRequest req = new UserProfileRequest("nickname", "userTagId", "avatar_url", "description", "background_url", "country", "city", false);

        when(userProfileRepository.existsByUserTagId(req.userTagId())).thenReturn(false);
        when(userProfileRepository.existsByAvatarUrl(req.avatar_url())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userProfileService.createUserProfile(req));
    }

    /**
     * Tests the createUserProfile method when the userTagId already exists but the avatar_url is unique.
     * Expected behavior: throw an IllegalArgumentException with the message "UserTagId already exists".
     */
    @Test
    public void test_createUserProfile_userTagIdExists() {
        UserProfileRequest request = new UserProfileRequest("nickname", "existingTag", "http://avatar.com", "description", "http://background.com", "country", "city", false);

        when(userProfileRepository.existsByUserTagId("existingTag")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userProfileService.createUserProfile(request), "UserTagId already exists");

        verify(userProfileRepository).existsByUserTagId("existingTag");
        verify(userProfileRepository, never()).existsByAvatarUrl(anyString());
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    /**
     * Test case for creating a user profile when the userTagId and avatar_url are unique.
     * This test verifies that a new user profile is successfully created and returned
     * when the provided userTagId and avatar_url do not already exist in the system.
     */
    @Test
    public void test_createUserProfile_whenUserTagIdAndAvatarUrlAreUnique() {
        // Arrange
        UserProfileRequest request = new UserProfileRequest(
                "testNickName",
                "testUserTagId",
                "http://test-avatar-url.com",
                "Test profile description",
                "http://test-background-url.com",
                "TestCountry",
                "TestCity",
                false
        );

        when(userProfileRepository.existsByUserTagId(request.userTagId())).thenReturn(false);
        when(userProfileRepository.existsByAvatarUrl(request.avatar_url())).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserProfileResponse response = userProfileService.createUserProfile(request);

        // Assert
        assertNotNull(response);
        // Add more assertions here to verify the response contents
    }

    /**
     * Test case for deleteUserProfile method when the user profile exists.
     * This test verifies that the deleteUserProfile method correctly deletes
     * an existing user profile.
     */
    @Test
    public void test_deleteExistingUserProfile() {
        String userTagId = "existingUser123";
        UserProfile mockUserProfile = new UserProfile();
        mockUserProfile.setUserTagId(userTagId);

        when(userProfileRepository.findByUserTagId(userTagId)).thenReturn(mockUserProfile);

        userProfileService.deleteUserProfile(userTagId);

        verify(userProfileRepository).delete(mockUserProfile);
    }


    /**
     * Test case for deleteUserProfile method when the user profile is not found.
     * This test verifies that an IllegalArgumentException is thrown when attempting
     * to delete a non-existent user profile.
     */
    @Test
    public void test_deleteUserProfile_whenUserProfileNotFound() {
        // Arrange
        UserProfileRepository mockRepository = Mockito.mock(UserProfileRepository.class);
        Mockito.when(mockRepository.findByUserTagId(Mockito.anyString())).thenReturn(null);
        UserProfileService userProfileService = new UserProfileService(mockRepository, null);

        String nonExistentUserTagId = "nonexistent_tag";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.deleteUserProfile(nonExistentUserTagId);
        });

        // Verify that delete was not called on the repository
        Mockito.verify(mockRepository, Mockito.never()).delete(Mockito.any());
    }

    /**
     * Tests the behavior of getUserProfileByNickName when the nickname does not exist.
     * This test verifies that the method returns null when no user profile is found for the given nickname.
     */
    @Test
    public void test_getUserProfileByNickName_nonExistentNickname() {
        MockitoAnnotations.openMocks(this);

        String nonExistentNickname = "nonexistent";
        when(userProfileRepository.findByNickName(nonExistentNickname)).thenReturn(null);

        UserProfileResponse response = userProfileService.getUserProfileByNickName(nonExistentNickname);

        assertNull(response);
    }

    /**
     * Test case for getUserProfileByNickName when the user profile does not exist.
     * This test verifies that the method returns null when no user profile is found for the given nickname.
     */
    @Test
    public void test_getUserProfileByNickName_returnsNullWhenProfileNotFound() {
        String nonExistentNickName = "nonexistent";
        when(userProfileRepository.findByNickName(nonExistentNickName)).thenReturn(null);

        UserProfileResponse result = userProfileService.getUserProfileByNickName(nonExistentNickName);

        assertNull(result, "Expected null response for non-existent nickname");
    }


    /**
     * Test case for getUserProfileByUserId when the user profile does not exist.
     * This test verifies that the method returns null when no user profile is found for the given UUID.
     */
    @Test
    public void test_getUserProfileByUserId_WhenUserProfileDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        UserProfileResponse result = userProfileService.getUserProfileByUserId(userId);

        assertNull(result);
    }

    /**
     * Tests the behavior of getUserProfileByUserId when the user ID does not exist in the repository.
     * This test verifies that the method returns null when no user profile is found for the given UUID.
     */
    @Test
    public void test_getUserProfileByUserId_nonExistentUser() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userProfileRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        UserProfileResponse response = userProfileService.getUserProfileByUserId(nonExistentUserId);

        assertNull(response, "The response should be null for a non-existent user ID");
    }

    /**
     * Test case for getUserProfileByUserTagId method when the userTagId doesn't exist.
     * This test verifies that the method returns null when no user profile is found.
     */
    @Test
    public void test_getUserProfileByUserTagId_nonExistentUser() {
        MockitoAnnotations.openMocks(this);

        String nonExistentUserTagId = "nonexistent123";
        when(userProfileRepository.findByUserTagId(nonExistentUserTagId)).thenReturn(null);

        UserProfileResponse response = userProfileService.getUserProfileByUserTagId(nonExistentUserTagId);

        assertNull(response, "Response should be null for non-existent user");
    }

    /**
     * Test case for getUserProfileByUserTagId when the user profile doesn't exist.
     * This test verifies that the method returns null when no user profile is found for the given userTagId.
     */
    @Test
    public void test_getUserProfileByUserTagId_returnsNullWhenUserProfileNotFound() {
        MockitoAnnotations.openMocks(this);

        String nonExistentUserTagId = "nonexistent123";
        when(userProfileRepository.findByUserTagId(nonExistentUserTagId)).thenReturn(null);

        UserProfileResponse result = userProfileService.getUserProfileByUserTagId(nonExistentUserTagId);

        assertNull(result, "Expected null response for non-existent user profile");
    }

    /**
     * Test case for getUserProfileByUserTagId when a user profile exists.
     * This test verifies that the method returns a non-null UserProfileResponse
     * when a valid userTagId is provided and a corresponding UserProfile is found.
     */
    @Test
    public void test_getUserProfileByUserTagId_whenUserProfileExists() {
        String userTagId = "validUserTagId";
        UserProfile mockUserProfile = new UserProfile();
        mockUserProfile.setNickName("TestNick");
        mockUserProfile.setUserTagId(userTagId);
        mockUserProfile.setAvatarUrl("http://avatar.com");
        mockUserProfile.setProfileDescription("desc");
        mockUserProfile.setBackground_url("http://bg.com");
        mockUserProfile.setCountry("UA");
        mockUserProfile.setCity("Kyiv");

        when(userProfileRepository.findByUserTagId(userTagId)).thenReturn(mockUserProfile);

        UserProfileResponse response = userProfileService.getUserProfileByUserTagId(userTagId);

        assertNotNull(response, "UserProfileResponse should not be null for an existing user profile");
        assertEquals("TestNick", response.nickName());
    }


    /**
     * Test subscribing to oneself, which should throw an IllegalArgumentException.
     * This test covers the path where subscriberId equals targetUserId.
     */
    @Test
    public void test_subscribeToUser_SelfSubscription() {
        UserProfileService userProfileService = new UserProfileService(null, null);
        UUID userId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.subscribeToUser(userId, userId);
        });
    }

    /**
     * Test that an IllegalStateException is thrown when a user tries to subscribe to a user they are already subscribed to.
     */
    @Test
    public void test_subscribeToUser_alreadySubscribed() {
        UUID subscriberId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        when(subscriptionRepository.findBySubscriberIdAndTargetUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(new Subscription()));

        assertThrows(IllegalStateException.class, () -> userProfileService.subscribeToUser(subscriberId, targetUserId));
    }

    /**
     * Test that an IllegalArgumentException is thrown when a user tries to subscribe to themselves.
     */
    @Test
    public void test_subscribeToUser_selfSubscription() {
        UUID userId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> userProfileService.subscribeToUser(userId, userId));
    }

    /**
     * Tests the subscribeToUser method when the subscriber is already subscribed to the target user.
     * Path constraints: !((subscriberId.equals(targetUserId))), (exists)
     */
    @Test
    public void test_subscribeToUser_whenAlreadySubscribed() {
        // Arrange
        UUID subscriberId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        SubscriptionRepository mockSubscriptionRepository = Mockito.mock(SubscriptionRepository.class);
        UserProfileService userProfileService = new UserProfileService(null, mockSubscriptionRepository);

        Mockito.when(mockSubscriptionRepository.findBySubscriberIdAndTargetUserId(subscriberId, targetUserId))
                .thenReturn(Optional.of(new Subscription()));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            userProfileService.subscribeToUser(subscriberId, targetUserId);
        });
    }

    /**
     * Test case for subscribing to a user when the subscriber and target are different users,
     * and the subscription does not already exist.
     */
    @Test
    public void test_subscribeToUser_whenSubscriberAndTargetAreDifferentAndSubscriptionDoesNotExist() {
        UUID subscriberId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        when(subscriptionRepository.findBySubscriberIdAndTargetUserId(subscriberId, targetUserId))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userProfileService.subscribeToUser(subscriberId, targetUserId));

        verify(subscriptionRepository).save(any(Subscription.class));
    }

    /**
     * Tests the updateUserProfile method when the user profile is not found.
     * This test verifies that the method returns null when no user profile
     * exists for the given userTagId.
     */
    @Test
    public void test_updateUserProfile_userNotFound() {
        String nonExistentUserTagId = "nonexistent123";
        UserProfileRequest req = new UserProfileRequest(
                "newNickname", nonExistentUserTagId, "newAvatar.jpg", "New description",
                "newBackground.jpg", "NewCountry", "NewCity", false
        );

        when(userProfileRepository.findByUserTagId(nonExistentUserTagId)).thenReturn(null);

        UserProfileResponse response = userProfileService.updateUserProfile(nonExistentUserTagId, req);

        assertNull(response, "Response should be null for non-existent user");
    }

}

