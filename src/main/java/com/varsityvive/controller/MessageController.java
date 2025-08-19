package com.varsityvive.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.varsityvive.dto.SendMessageRequest;
import com.varsityvive.model.Message;
import com.varsityvive.model.User;
import com.varsityvive.repository.UserRepository;
import com.varsityvive.service.MessageService;


@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<Message>> getConversation(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        
        User otherUser = userRepository.findById(userId).orElse(null);
        if (otherUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Message> conversation = messageService.getConversation(currentUser, otherUser);
        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestBody SendMessageRequest request) { // Changed to use DTO
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User sender = userRepository.findByEmail(email).orElse(null);
        
        if (sender == null) {
            return ResponseEntity.status(401).build();
        }
        
        User receiver = userRepository.findById(request.getReceiverId()).orElse(null);
        if (receiver == null) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = messageService.sendMessage(sender, receiver, request.getContent());
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/inbox")
    public ResponseEntity<List<Message>> getUserInbox() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        
        List<Message> inbox = messageService.getUserInbox(currentUser);
        return ResponseEntity.ok(inbox);
    }
}