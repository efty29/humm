package com.varsityvive.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.varsityvive.model.Post;
import com.varsityvive.model.User;
import com.varsityvive.repository.PostRepository;
import com.varsityvive.repository.UserRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByTimestampDesc();
    }

    public Post createPost(Post post, MultipartFile mediaFile, String category) throws IOException {
        User user = userRepository.findById(post.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        post.setUser(user);
        post.setTimestamp(new Date());
        post.setCategory(category); // Set category
        
        if (mediaFile != null && !mediaFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + mediaFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Files.copy(mediaFile.getInputStream(), uploadPath.resolve(fileName));
            post.setMedia(fileName);
            post.setMediaType(mediaFile.getContentType());
        }

        return postRepository.save(post);
    }
}