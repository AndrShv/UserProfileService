package org.example.service;

import jakarta.websocket.server.ServerEndpoint;
import org.example.event.UserRegisteredEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class RabbitConsumer {


    @RabbitListener(queues = "${queue.name}")
    public void receiveFromAuth(UserRegisteredEvent event){
        System.out.println("Получено событие регистрации:");
        System.out.println("Email: " + event.getEmail());
        System.out.println("Username: " + event.getUsername());

    }
}
