package com.varsityvive.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.varsityvive.model.Message;
import com.varsityvive.model.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrReceiverAndSender(
        User sender1, User receiver1,
        User sender2, User receiver2
    );
    
    List<Message> findByReceiver(User receiver);
}
