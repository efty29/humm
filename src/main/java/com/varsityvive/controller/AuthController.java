package com.varsityvive.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.varsityvive.dto.LoginRequest;
import com.varsityvive.dto.OtpVerifyDTO;
import com.varsityvive.model.User;
import com.varsityvive.repository.UserRepository;
import com.varsityvive.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5000" })
public class AuthController {

    private static final Pattern VARSITY_EMAIL = Pattern.compile("^[^@]+@diu\\.edu\\.bd$", Pattern.CASE_INSENSITIVE);

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private final Map<String, OtpVerifyDTO> pendingSignups = new ConcurrentHashMap<>();

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody OtpVerifyDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (dto.getEmail() == null || dto.getEmail().isBlank()
             || dto.getPassword() == null || dto.getPassword().isBlank()
             || dto.getName() == null || dto.getName().isBlank()) {
                response.put("status", "error");
                response.put("code", "VALIDATION_FAILED");
                response.put("message", "Name, email, and password are required");
                return ResponseEntity.badRequest().body(response);
            }

            String normalizedEmail = dto.getEmail().trim().toLowerCase();
            if (!VARSITY_EMAIL.matcher(normalizedEmail).matches()) {
                response.put("status", "error");
                response.put("code", "INVALID_EMAIL_DOMAIN");
                response.put("message", "Only university emails allowed (…@diu.edu.bd)");
                return ResponseEntity.badRequest().body(response);
            }
            if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                response.put("status", "error");
                response.put("code", "USER_EXISTS");
                response.put("message", "User already exists with this email");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            dto.setEmail(normalizedEmail);
            dto.setPassword(dto.getPassword().strip());

            authService.sendOtpToEmail(normalizedEmail);
            pendingSignups.put(normalizedEmail, dto);

            response.put("status", "success");
            response.put("message", "OTP sent to your email");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("code", "VALIDATION_FAILED");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("code", "EMAIL_DELIVERY_FAILED");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email") == null ? null : payload.get("email").trim().toLowerCase();
        String otp = payload.get("otp");

        Map<String, Object> response = new HashMap<>();
        try {
            if (!authService.verifyOtp(email, otp)) {
                throw new IllegalArgumentException("Invalid or expired OTP");
            }

            OtpVerifyDTO signupData = pendingSignups.remove(email);
            if (signupData == null) {
                throw new IllegalStateException("No signup data found for this email");
            }

            String result = authService.createUserAfterOtp(signupData);
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = loginRequest.getEmail() == null ? null : loginRequest.getEmail().trim().toLowerCase();
            String rawPassword = loginRequest.getPassword() == null ? "" : loginRequest.getPassword().strip();

            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
            if (userOpt.isEmpty()) throw new RuntimeException("User not found");

            User user = userOpt.get();
            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("department", user.getDepartment());
            response.put("batch", user.getBatch());
            response.put("section", user.getSection());
            response.put("profilePic", user.getProfilePic());
            response.put("coverPic", user.getCoverPic());
            response.put("bio", user.getBio());
            response.put("universityId", user.getUniversityId());
            response.put("universityEmail", user.getUniversityEmail());
            response.put("bloodGroup", user.getBloodGroup());
            response.put("phoneNumber", user.getPhoneNumber());
            response.put("role", user.getRole().name());
            response.put("friends", new ArrayList<>());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // ---------------------------
    // TEMP DEBUG ENDPOINT
    // ---------------------------
    @PostMapping("/_debug-check")
    public ResponseEntity<?> debugCheck(@RequestBody LoginRequest body) {
        String email = body.getEmail() == null ? null : body.getEmail().trim().toLowerCase();
        String raw = body.getPassword() == null ? "" : body.getPassword().strip();

        Map<String, Object> out = new HashMap<>();
        Optional<User> u = userRepository.findByEmailIgnoreCase(email);
        if (u.isEmpty()) {
            out.put("foundUser", false);
            return ResponseEntity.ok(out);
        }
        User user = u.get();
        boolean matched = passwordEncoder.matches(raw, user.getPassword());

        out.put("foundUser", true);
        out.put("email", user.getEmail());
        out.put("storedHashPrefix", user.getPassword() == null ? null : user.getPassword().substring(0, 7));
        out.put("rawLen", raw.length());
        out.put("matches", matched);
        out.put("role", user.getRole().name());
        out.put("phoneNumber", user.getPhoneNumber());
        out.put("section", user.getSection());
        return ResponseEntity.ok(out);
    }
}
