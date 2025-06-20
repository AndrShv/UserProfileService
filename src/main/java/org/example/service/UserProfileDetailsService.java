package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.UserProfile;
import org.example.repository.UserProfileRepository;
import org.example.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserProfileDetailsService implements UserDetailsService {

    private final UserProfileRepository userProfileRepository;


    @Override
    public UserDetails loadUserByUsername(String userIdOrEmail) throws UsernameNotFoundException {
        UUID uuid;
        try {
            uuid = UUID.fromString(userIdOrEmail);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid UUID format: " + userIdOrEmail);
        }
        return userProfileRepository.findById(uuid)
                .map(UserProfileDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userIdOrEmail));
    }



}

