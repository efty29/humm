package com.varsityvive.controller;

import com.varsityvive.model.Message;
import com.varsityvive.model.User;
import com.varsityvive.repository.UserRepository;
import com.varsityvive.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-donation")
public class BloodDonorController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<User> registerAsDonor(@RequestParam String bloodGroup) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? auth.getName() : null;

        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        user.setBloodGroup(bloodGroup);
        User updated = userRepository.save(user);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/donors")
    public ResponseEntity<List<User>> getDonorsByBloodGroup(@RequestParam String bloodGroup) {
        List<User> donors = userRepository.findByBloodGroup(bloodGroup);
        return ResponseEntity.ok(donors);
    }

    @PostMapping("/contact/{donorId}")
    public ResponseEntity<Message> contactDonor(
            @PathVariable Long donorId,
            @RequestParam String message) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? auth.getName() : null;

        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        User requester = userRepository.findByEmail(email).orElse(null);
        User donor = userRepository.findById(donorId).orElse(null);

        if (requester == null || donor == null) {
            return ResponseEntity.status(404).build();
        }

        Message sentMessage = messageService.sendMessage(requester, donor, message);
        return ResponseEntity.ok(sentMessage);
    }
}
