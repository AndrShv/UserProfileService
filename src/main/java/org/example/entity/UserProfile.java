package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;


    @Column(name = "avatar_url", unique = true)
    private String avatarUrl;

    @Column(name = "profile_description")
    private String profileDescription;

    @Column(name = "background_url")
    private String background_url;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;


}
