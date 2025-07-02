package org.example.security;

import lombok.Getter;
import org.example.entity.UserProfile;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final UUID id;
    private final String email;

    public UserDetailsImpl(UserProfile user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }

    @Override
    public Collection getAuthorities() {
        return null;  // без ролей — возвращаем null или Collections.emptyList()
    }

    @Override
    public String getPassword() {
        return null;  // пароля нет
    }

    @Override
    public String getUsername() {
        return email;  // email — это username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // без логики
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // без логики
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // без логики
    }

    @Override
    public boolean isEnabled() {
        return true;  // без логики
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDetailsImpl)) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
