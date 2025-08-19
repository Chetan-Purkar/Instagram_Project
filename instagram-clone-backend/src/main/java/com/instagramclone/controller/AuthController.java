package com.instagramclone.controller;

import com.instagramclone.dto.AuthRequest;
import com.instagramclone.dto.AuthResponse;
import com.instagramclone.model.User;
import com.instagramclone.security.JwtUtil;
import com.instagramclone.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        String message = authService.signup(user);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(authRequest);

        // Set token in response header
        response.setHeader("Authorization", "Bearer " + authResponse.getToken());
        System.out.println("Generated Token: " + authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }
  

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e) {
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
   
}