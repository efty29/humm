package com.varsityvive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class EmailTest {

    @Autowired
    private JavaMailSender mailSender;

    @Test
    void testEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("your-real-email@diu.edu.bd"); // Use your actual test email
            message.setSubject("SMTP Connection Test");
            message.setText("If you receive this, SMTP is working!");
            
            mailSender.send(message);
            System.out.println("Test email sent successfully!");
        } catch (Exception e) {
            System.err.println("Failed to send test email:");
            e.printStackTrace();
        }
    }
}