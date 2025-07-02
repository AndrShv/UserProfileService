package org.example.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreatedEvent {
    private UUID videoId;
    private UUID commentId;
    private UUID authorId;
    private String text;
    private LocalDateTime createdAt;
}

