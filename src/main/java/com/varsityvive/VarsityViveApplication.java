package com.varsityvive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.varsityvive.service.AuthService;

@SpringBootApplication
public class VarsityViveApplication implements CommandLineRunner {

    @Autowired
    private AuthService authService;
    
    public static void main(String[] args) {
        SpringApplication.run(VarsityViveApplication.class, args);
    }

    @Override
    public void run(String... args) {
        testEmailSending();
    }
    
    private void testEmailSending() {
        try {
            authService.sendOtpToEmail("test@diu.edu.bd");
            System.out.println("✅ Email test successful!");
        } catch (Exception e) {
            System.err.println("❌ Email test failed: " + e.getMessage());
        }
    }
}