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
    @GeneratedValue(generator = "UUID")
    @org.hibernate.annotations.GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String nickName;

    @Column(name = "user_tag_id", nullable = false, unique = true)
    private String userTagId;

    @Column(name = "avatar_url", nullable = false, unique = true)
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
