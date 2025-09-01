package com.instagramclone.controller;

import com.instagramclone.dto.AuthRequest;
import com.instagramclone.dto.AuthResponse;
import com.instagramclone.model.User;
import com.instagramclone.repository.UserRepository;
import com.instagramclone.security.JwtUtil;
import com.instagramclone.service.AuthService;
import com.instagramclone.service.OtpService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, JwtUtil jwtUtil,
                          OtpService otpService, UserRepository userRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
        this.userRepository = userRepository;
    }

    // Signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        return ResponseEntity.ok(authService.signup(user));
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest,
                                              HttpServletResponse response) {
        AuthResponse authResponse = authService.login(authRequest);
        response.setHeader("Authorization", "Bearer " + authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }
    
 // Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        // Remove Authorization header on client (optional, since JWT is stateless)
        response.setHeader("Authorization", null);

        // Optionally, you can implement server-side token blacklist in future
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }


    // Request OTP
    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found"));

        try {
            otpService.requestOtp(email);
            return ResponseEntity.ok(Map.of("message", "OTP sent"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String otp = body.get("otp");

        if (!otpService.verifyOtp(email, otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired OTP"));
        }

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token, "message", "Login successful"));
    }

    // Resend OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        try {
            otpService.requestOtp(email);
            return ResponseEntity.ok(Map.of("message", "OTP resent"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Global exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e) {
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}
