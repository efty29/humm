package com.varsityvive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.varsityvive.dto.TypingNotification;
import com.varsityvive.model.Message;


@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Message message) {
        // Save message to database (you might want to inject MessageService here)
        
        // Send to specific user
        messagingTemplate.convertAndSendToUser(
            message.getReceiver().getId().toString(), 
            "/queue/messages", 
            message
        );
        
        // Also send to sender for their own UI update
        messagingTemplate.convertAndSendToUser(
            message.getSender().getId().toString(),
            "/queue/messages",
            message
        );
    }

    @MessageMapping("/chat.typing")
    public void typingNotification(@Payload TypingNotification notification) {
        messagingTemplate.convertAndSendToUser(
            notification.getReceiverId().toString(),
            "/queue/typing",
            notification
        );
    }
}