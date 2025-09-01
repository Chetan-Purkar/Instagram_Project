package com.instagramclone.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.instagramclone.model.EmailOtp;
import com.instagramclone.repository.EmailOtpRepository;

@Service
public class OtpService {
    private final EmailService emailService;
    private final EmailOtpRepository otpRepository; // JPA repo or use Redis client
    private final int otpLength = 6;
    private final Duration otpValidity = Duration.ofMinutes(5);
    private final int maxRequestsPerHour = 5;
    private final int maxVerifyAttempts = 5;

    public OtpService(EmailService emailService, EmailOtpRepository otpRepository) {
        this.emailService = emailService;
        this.otpRepository = otpRepository;
    }

    public void requestOtp(String email) {
        // Rate limiting: count OTPs requested in last hour
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentCount = otpRepository.countByEmailAndCreatedAtAfter(email, oneHourAgo);
        if (recentCount >= maxRequestsPerHour) {
            throw new IllegalStateException("Too many OTP requests. Try again later.");
        }

        String otp = generateNumericOtp(otpLength);
        LocalDateTime now = LocalDateTime.now();
        EmailOtp entry = new EmailOtp();
        entry.setEmail(email);
        entry.setOtp(otp);
        entry.setCreatedAt(now);
        entry.setExpiresAt(now.plus(otpValidity));
        entry.setAttempts(0);
        entry.setUsed(false);
        otpRepository.save(entry);

        emailService.sendOtpEmail(email, otp, otpValidity);
    }

    public boolean verifyOtp(String email, String submittedOtp) {
        // Use Optional instead of List
        Optional<EmailOtp> otpOpt = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (otpOpt.isEmpty()) return false;

        EmailOtp otpEntry = otpOpt.get();

        if (otpEntry.isUsed() || otpEntry.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        if (otpEntry.getAttempts() >= maxVerifyAttempts) {
            return false;
        }

        if (otpEntry.getOtp().equals(submittedOtp)) {
            otpEntry.setUsed(true);
            otpRepository.save(otpEntry);
            return true;
        } else {
            otpEntry.setAttempts(otpEntry.getAttempts() + 1);
            otpRepository.save(otpEntry);
            return false;
        }
    }


    private String generateNumericOtp(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }
}
