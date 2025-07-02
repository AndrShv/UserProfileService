package org.example.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.event.CommentCreatedEvent;
import org.example.repository.UserProfileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventListener {
    private final UserProfileRepository userProfileRepository;

    @RabbitListener(queues = "comment.queue")
    public void handleCommentCreated(CommentCreatedEvent event) {
        log.info("🗨️ New comment event received: videoId={}, commentId={}, authorId={}, text='{}'",
                event.getVideoId(), event.getCommentId(), event.getText(), event.getText());

        System.out.println("New comment event received: ");
        System.out.println("Video ID: " + event.getVideoId());
        System.out.println("Comment ID: " + event.getCommentId());
        System.out.println("Author ID: " + event.getAuthorId());
        System.out.println("Comment text: " + event.getText());
        System.out.println("Сохранено в базу даних: " + event.getCreatedAt());


        // Тут можна, наприклад:
        // - оновити статистику відео (збільшити кількість коментарів)
        // - зберегти в окрему колекцію/таблицю
        // - надіслати нотифікацію автору відео
    }
}

