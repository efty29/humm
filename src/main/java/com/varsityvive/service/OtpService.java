package com.varsityvive.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private static final long EXPIRY_TIME_MS = 5 * 60 * 1000; // 5 minutes
    private static final long COOLDOWN_MS = 60 * 1000; // 1 minute
    private final Map<String, OtpEntry> otpStorage = new HashMap<>();

    private static class OtpEntry {
        String otp;
        long timestamp;
        OtpEntry(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }

    public boolean isOtpRequestAllowed(String email) {
        OtpEntry entry = otpStorage.get(email);
        if (entry == null) return true;
        long timeSinceLastRequest = System.currentTimeMillis() - entry.timestamp;
        return timeSinceLastRequest > COOLDOWN_MS;
    }

    public String generateOtp(String email) {
        if (!isOtpRequestAllowed(email)) {
            throw new IllegalStateException("Please wait 1 minute before requesting a new OTP");
        }
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, new OtpEntry(otp, System.currentTimeMillis()));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpEntry entry = otpStorage.get(email);
        if (entry == null || !entry.otp.equals(otp)) {
            return false;
        }
        return System.currentTimeMillis() - entry.timestamp <= EXPIRY_TIME_MS;
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
    }
}
