package com.varsityvive.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.varsityvive.model.User;
import com.varsityvive.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username == email
        String email = username == null ? null : username.trim();
        User u = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // If this email was created via Google OAuth (password empty), block password login explicitly
        if (u.getPassword() == null || u.getPassword().isBlank()) {
            throw new UsernameNotFoundException("This account uses Google sign-in. Use Google or reset password.");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail().toLowerCase()) // normalize
                .password(u.getPassword())                 // BCrypt hash from DB
                .roles("USER")
                .build();
    }
}
