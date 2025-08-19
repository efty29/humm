package com.varsityvive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String redirectAddress;
    private final boolean redirectEnabled;

    public EmailService(JavaMailSender mailSender, 
                       @Value("${spring.mail.redirect-address:}") String redirectAddress) {
        this.mailSender = mailSender;
        this.redirectAddress = redirectAddress;
        this.redirectEnabled = !redirectAddress.isEmpty();
    }

    public void sendOtp(String to, String otp) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("jahid241-15-759@diu.edu.bd");

        String actualTo = redirectEnabled ? redirectAddress : to;
        message.setTo(actualTo);
        message.setSubject("VarsityVive Verification Code");
        message.setText("Your OTP code is: " + otp +
                "\nThis code will expire in 5 minutes." +
                (redirectEnabled ? "\n\n[Original recipient: " + to + "]" : ""));

        mailSender.send(message);
        System.out.println("Email sent to: " + actualTo);
    }
}
