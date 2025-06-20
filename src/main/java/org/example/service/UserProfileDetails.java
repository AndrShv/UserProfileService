package org.example.service;

import org.example.entity.UserProfile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserProfileDetails implements UserDetails {

    private final UserProfile userProfile;

    public UserProfileDetails(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Пока без ролей — пустой список
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return userProfile.getId().toString();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true; // всегда true, если не используешь логику
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // всегда true, если не используешь логику
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // всегда true, если не используешь логику
    }

    @Override
    public boolean isEnabled() {
        return true; // всегда true, если не используешь логику
    }

    public UUID getId() {
        return userProfile.getId();
    }

    // Можно добавить геттеры для других полей UserProfile, если нужно
}

