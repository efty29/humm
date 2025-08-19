package com.varsityvive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.varsityvive.dto.OtpVerifyDTO;
import com.varsityvive.model.User;
import com.varsityvive.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void sendOtpToEmail(String email) throws Exception {
        if (!isValidDiuEmail(email)) {
            throw new IllegalArgumentException("Only valid DIU emails allowed (@diu.edu.bd)");
        }
        String normalized = email.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String otp = otpService.generateOtp(normalized);
        try {
            emailService.sendOtp(normalized, otp);
        } catch (MailException e) {
            throw new Exception("Email delivery failed: " + e.getMessage(), e);
        }
    }

    private boolean isValidDiuEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@diu\\.edu\\.bd$");
    }

    public String verifyOtpAndCreateUser(OtpVerifyDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Signup data missing");
        if (dto.getEmail() == null || dto.getEmail().isBlank()
         || dto.getPassword() == null || dto.getPassword().isBlank()
         || dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Name, email, and password are required");
        }
        String email = dto.getEmail().trim().toLowerCase();
        if (!otpService.validateOtp(email, dto.getOtp())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        return createUserInternal(dto);
    }

    public boolean verifyOtp(String email, String otp) {
        String normalized = (email == null) ? null : email.trim().toLowerCase();
        return otpService.validateOtp(normalized, otp);
    }

    public String createUserAfterOtp(OtpVerifyDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Signup data missing. Please restart signup.");
        if (dto.getEmail() == null || dto.getEmail().isBlank()
         || dto.getPassword() == null || dto.getPassword().isBlank()
         || dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Name, email, and password are required");
        }
        return createUserInternal(dto);
    }

    // Internal user creation
    private String createUserInternal(OtpVerifyDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(dto.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(dto.getPassword().strip())); // strip before encode
        user.setDepartment(dto.getDepartment());
        user.setBatch(dto.getBatch());
        user.setSection(dto.getSection());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setVerified(true);
        user.setUniversityEmail(email);
        user.setUniversityId(generateUniversityId(email));

        userRepository.save(user);//poly
        otpService.clearOtp(email);

        return "User registered successfully";
    }

    private String generateUniversityId(String email) {
        String prefix = email.split("@")[0];
        return "STU_" + prefix.replace("-", "");
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
