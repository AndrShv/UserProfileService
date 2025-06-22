package org.example.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCreatingEvent implements Serializable {
    private UUID authorId;
    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private Long duration;
    private String category;
}
