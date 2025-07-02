package main.org.example.service;
 import org.example.DTO.request.UserProfileRequest;
 import org.example.DTO.response.UserProfileResponse;
 import org.example.entity.Subscription;
 import org.example.entity.UserProfile;
 import org.example.event.UserRegisteredEvent;
 import org.example.repository.SubscriptionRepository;
 import org.example.repository.UserProfileRepository;
 import org.example.service.UserProfileService;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Nested;
 import org.junit.jupiter.api.Test;
 import org.mockito.ArgumentCaptor;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.MockitoAnnotations;

 import java.time.LocalDateTime;
 import java.util.List;
 import java.util.Optional;
 import java.util.UUID;

 import static org.assertj.core.api.Assertions.*;
 import static org.mockito.Mockito.*;

 class UserProfileServiceTest {

     @Mock
     private UserProfileRepository userProfileRepository;
     @Mock
     private SubscriptionRepository subscriptionRepository;

     @InjectMocks
     private UserProfileService userProfileService;

     private final UUID userId = UUID.randomUUID();
     private final UUID anotherUserId = UUID.randomUUID();

     @BeforeEach
     void setUp() {
         MockitoAnnotations.openMocks(this);
     }

     @Test
     void createUserProfile_CreatesProfile_WhenEmailNotExists() {
         UserRegisteredEvent event = new UserRegisteredEvent(userId, "user@email.com", "user");
         when(userProfileRepository.existsByEmail(event.getEmail())).thenReturn(false);
         when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

         UserProfileResponse response = userProfileService.createUserProfile(event);

         assertThat(response.id()).isEqualTo(userId);
         assertThat(response.username()).isEqualTo("user");
         assertThat(response.email()).isEqualTo("user@email.com");
     }

     @Test
     void createUserProfile_ThrowsException_WhenEmailAlreadyExists() {
         UserRegisteredEvent event = new UserRegisteredEvent(userId, "user@email.com", "user");
         when(userProfileRepository.existsByEmail(event.getEmail())).thenReturn(true);

         assertThatThrownBy(() -> userProfileService.createUserProfile(event))
                 .isInstanceOf(IllegalArgumentException.class)
                 .hasMessageContaining("already exists");
     }

     @Test
     void getUserProfileById_ReturnsProfile_WhenExists() {
         UserProfile profile = new UserProfile();
         profile.setId(userId);
         profile.setUsername("user");
         profile.setEmail("user@email.com");
         when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));

         UserProfileResponse response = userProfileService.getUserProfileById(userId);

         assertThat(response.id()).isEqualTo(userId);
         assertThat(response.username()).isEqualTo("user");
     }

     @Test
     void getUserProfileById_ReturnsNull_WhenNotExists() {
         when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

         UserProfileResponse response = userProfileService.getUserProfileById(userId);

         assertThat(response).isNull();
     }

     @Test
     void getUserProfileByUserName_ReturnsProfile_WhenExists() {
         UserProfile profile = new UserProfile();
         profile.setId(userId);
         profile.setUsername("user");
         profile.setEmail("user@email.com");
         when(userProfileRepository.findByUsername("user")).thenReturn(profile);

         UserProfileResponse response = userProfileService.getUserProfileByUserName("user");

         assertThat(response.id()).isEqualTo(userId);
         assertThat(response.username()).isEqualTo("user");
     }

     @Test
     void getUserProfileByUserName_ReturnsNull_WhenNotExists() {
         when(userProfileRepository.findByUsername("user")).thenReturn(null);

         UserProfileResponse response = userProfileService.getUserProfileByUserName("user");

         assertThat(response).isNull();
     }

     @Test
     void updateUserProfile_UpdatesProfile_WhenExists() {
         UserProfile profile = new UserProfile();
         profile.setId(userId);
         when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
         UserProfileRequest req = mock(UserProfileRequest.class);
         when(req.username()).thenReturn("newuser");
         when(req.avatar_url()).thenReturn("avatar");
         when(req.profileDescription()).thenReturn("desc");
         when(req.background_url()).thenReturn("bg");
         when(req.country()).thenReturn("country");
         when(req.city()).thenReturn("city");
         when(req.isPrivate()).thenReturn(true);

         UserProfileResponse response = userProfileService.updateUserProfile(userId, req);

         assertThat(response.username()).isEqualTo("newuser");
         assertThat(response.avatar_url()).isEqualTo("avatar");
         assertThat(response.profileDescription()).isEqualTo("desc");
         assertThat(response.background_url()).isEqualTo("bg");
         assertThat(response.country()).isEqualTo("country");
         assertThat(response.city()).isEqualTo("city");
         assertThat(response.isPrivate()).isTrue();
     }

     @Test
     void updateUserProfile_ReturnsNull_WhenProfileNotExists() {
         when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());
         UserProfileRequest req = mock(UserProfileRequest.class);

         UserProfileResponse response = userProfileService.updateUserProfile(userId, req);

         assertThat(response).isNull();
     }

     @Test
     void deleteUserProfile_DeletesProfile_WhenExists() {
         UserProfile profile = new UserProfile();
         profile.setId(userId);
         when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));

         userProfileService.deleteUserProfile(userId);

         verify(userProfileRepository).delete(profile);
     }

     @Test
     void deleteUserProfile_ThrowsException_WhenProfileNotExists() {
         when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

         assertThatThrownBy(() -> userProfileService.deleteUserProfile(userId))
                 .isInstanceOf(IllegalArgumentException.class)
                 .hasMessageContaining("not found");
     }

     @Test
     void subscribeToUser_Subscribes_WhenNotAlreadySubscribed() {
         when(subscriptionRepository.findBySubscriberIdAndTargetUserId(userId, anotherUserId)).thenReturn(Optional.empty());

         userProfileService.subscribeToUser(userId, anotherUserId);

         verify(subscriptionRepository).save(any(Subscription.class));
     }

     @Test
     void subscribeToUser_ThrowsException_WhenSubscribingToSelf() {
         assertThatThrownBy(() -> userProfileService.subscribeToUser(userId, userId))
                 .isInstanceOf(IllegalArgumentException.class)
                 .hasMessageContaining("cannot subscribe to yourself");
     }

     @Test
     void subscribeToUser_ThrowsException_WhenAlreadySubscribed() {
         when(subscriptionRepository.findBySubscriberIdAndTargetUserId(userId, anotherUserId)).thenReturn(Optional.of(new Subscription()));

         assertThatThrownBy(() -> userProfileService.subscribeToUser(userId, anotherUserId))
                 .isInstanceOf(IllegalStateException.class)
                 .hasMessageContaining("Already subscribed");
     }

     @Test
     void unsubscribeFromUser_DeletesSubscription() {
         userProfileService.unsubscribeFromUser(userId, anotherUserId);

         verify(subscriptionRepository).deleteBySubscriberIdAndTargetUserId(userId, anotherUserId);
     }

     @Test
     void getSubscribersForUser_ReturnsListOfSubscribers() {
         Subscription sub1 = new Subscription();
         sub1.setSubscriberId(UUID.randomUUID());
         Subscription sub2 = new Subscription();
         sub2.setSubscriberId(UUID.randomUUID());
         when(subscriptionRepository.findAllByTargetUserId(userId)).thenReturn(List.of(sub1, sub2));

         List<UUID> result = userProfileService.getSubscribersForUser(userId);

         assertThat(result).containsExactly(sub1.getSubscriberId(), sub2.getSubscriberId());
     }

     @Test
     void getSubscribedUsers_ReturnsListOfSubscribedUsers() {
         Subscription sub1 = new Subscription();
         sub1.setTargetUserId(UUID.randomUUID());
         Subscription sub2 = new Subscription();
         sub2.setTargetUserId(UUID.randomUUID());
         when(subscriptionRepository.findAllBySubscriberId(userId)).thenReturn(List.of(sub1, sub2));

         List<UUID> result = userProfileService.getSubscribedUsers(userId);

         assertThat(result).containsExactly(sub1.getTargetUserId(), sub2.getTargetUserId());
     }

     @Test
     void canAccessProfile_ReturnsTrue_WhenProfileIsNotPrivate() {
         UserProfile profile = new UserProfile();
         profile.setId(anotherUserId);
         profile.setPrivate(false);
         when(userProfileRepository.findById(anotherUserId)).thenReturn(Optional.of(profile));

         boolean result = userProfileService.canAccessProfile(userId, anotherUserId);

         assertThat(result).isTrue();
     }

     @Test
     void canAccessProfile_ReturnsTrue_WhenProfileIsPrivateAndSubscribed() {
         UserProfile profile = new UserProfile();
         profile.setId(anotherUserId);
         profile.setPrivate(true);
         when(userProfileRepository.findById(anotherUserId)).thenReturn(Optional.of(profile));
         when(subscriptionRepository.existsBySubscriberIdAndTargetUserId(userId, anotherUserId)).thenReturn(true);

         boolean result = userProfileService.canAccessProfile(userId, anotherUserId);

         assertThat(result).isTrue();
     }

     @Test
     void canAccessProfile_ReturnsFalse_WhenProfileIsPrivateAndNotSubscribed() {
         UserProfile profile = new UserProfile();
         profile.setId(anotherUserId);
         profile.setPrivate(true);
         when(userProfileRepository.findById(anotherUserId)).thenReturn(Optional.of(profile));
         when(subscriptionRepository.existsBySubscriberIdAndTargetUserId(userId, anotherUserId)).thenReturn(false);

         boolean result = userProfileService.canAccessProfile(userId, anotherUserId);

         assertThat(result).isFalse();
     }
 }
