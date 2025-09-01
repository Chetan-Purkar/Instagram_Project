package com.instagramclone.repository;

import com.instagramclone.model.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findByEmailAndOtp(String email, String otp);
    Optional<EmailOtp> findTopByEmailOrderByCreatedAtDesc(String email); // latest OTP
    
    long countByEmailAndCreatedAtAfter(String email, LocalDateTime createdAt);
}
