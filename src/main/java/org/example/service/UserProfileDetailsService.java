package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.UserProfile;
import org.example.repository.UserProfileRepository;
import org.example.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserProfileDetailsService implements UserDetailsService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProfile userProfile = userProfileRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new UserDetailsImpl(userProfile);
    }
}

