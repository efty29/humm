package com.varsityvive.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varsityvive.model.Club;
import com.varsityvive.model.Post;
import com.varsityvive.model.User;
import com.varsityvive.repository.ClubRepository;
import com.varsityvive.repository.UserRepository;
import com.varsityvive.service.PostService;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Club> createClub(@RequestBody Club club) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User organizer = userRepository.findByEmail(email).orElse(null);
        
        if (organizer == null) {
            return ResponseEntity.status(401).build();
        }
        
        club.setOrganizer(organizer);
        club.getMembers().add(organizer);
        Club saved = clubRepository.save(club);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    @PostMapping("/{clubId}/join")
    public ResponseEntity<Club> joinClub(@PathVariable Long clubId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        Optional<Club> clubOpt = clubRepository.findById(clubId);
        
        if (user == null || !clubOpt.isPresent()) {
            return ResponseEntity.status(401).build();
        }
        
        Club club = clubOpt.get();
        if (!club.getMembers().contains(user)) {
            club.getMembers().add(user);
        }
        
        Club updated = clubRepository.save(club);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{clubId}/posts")
    public ResponseEntity<List<Post>> getClubPosts(@PathVariable Long clubId) {
        Optional<Club> clubOpt = clubRepository.findById(clubId);
        if (!clubOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(clubOpt.get().getPosts());
    }
    
    @PostMapping("/{clubId}/posts")
    public ResponseEntity<Post> createClubPost(
            @PathVariable Long clubId,
            @RequestPart("post") String postJson,
            @RequestPart(value = "media", required = false) MultipartFile mediaFile) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            Optional<Club> clubOpt = clubRepository.findById(clubId);
            
            if (user == null || !clubOpt.isPresent()) {
                return ResponseEntity.status(401).build();
            }
            
            Post post = objectMapper.readValue(postJson, Post.class);
            post.setUser(user);
            post.setClub(clubOpt.get());
            post.setCategory("CLUB");
            
            Post newPost = postService.createPost(post, mediaFile, "CLUB");
            return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}