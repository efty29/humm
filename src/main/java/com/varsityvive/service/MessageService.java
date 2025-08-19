package com.varsityvive.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varsityvive.model.Message;
import com.varsityvive.model.User;
import com.varsityvive.repository.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(User sender, User receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(new Date());
        message.setRead(false);
        return messageRepository.save(message);//poly
    }

    public List<Message> getConversation(User user1, User user2) {
        List<Message> messages = messageRepository
            .findBySenderAndReceiverOrReceiverAndSender(user1, user2, user2, user1);
        return messages.stream()
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<Message> getUserInbox(User user) {
        return messageRepository.findByReceiver(user);
    }
}
