package com.instagramclone.controller;

import com.instagramclone.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/toggle/{postId}")
    public ResponseEntity<String> toggleLike(@PathVariable Long postId, @RequestParam String username) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Ensure only current user can toggle their own likes
        if (!currentUsername.equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: You can only like/unlike as the logged-in user.");
        }

        boolean liked = likeService.toggleLike(postId, username);
        return ResponseEntity.ok(liked ? "Liked" : "Unliked");
    }
}
