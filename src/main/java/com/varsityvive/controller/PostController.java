package com.varsityvive.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varsityvive.model.Post;
import com.varsityvive.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    // GET all posts
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    // POST create post with optional media file
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestPart("post") String postJson,
            @RequestPart(value = "media", required = false) MultipartFile mediaFile) {
        try {
            // Deserialize JSON string to Post object
            Post post = objectMapper.readValue(postJson, Post.class);

            // Get the authenticated username
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Save the post
            Post newPost = postService.createPost(post, mediaFile, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Post creation failed: " + e.getMessage());
        }
    }
}
