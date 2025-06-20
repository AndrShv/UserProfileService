package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.CollectionId;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.events.Event;

import java.util.UUID;

@Entity
@Getter
@Service
@Table(name = "profile_videos")
public class ProfileVideo {
    @Id
    private UUID id;
    @Column(nullable = false, name = "author_id")
    private UUID authorId;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "video_url", unique = true, nullable = false)
    private String videoUrl;
}
