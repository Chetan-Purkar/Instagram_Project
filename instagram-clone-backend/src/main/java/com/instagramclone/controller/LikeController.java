package com.instagramclone.controller;

import com.instagramclone.service.LikeService;
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

    /**
     * Toggle like/unlike for a post
     */
    @PostMapping("/toggle/{postId}")
    public ResponseEntity<String> toggleLike(@PathVariable Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName(); // Always use authenticated user

        boolean liked = likeService.toggleLike(postId, currentUsername);
        return ResponseEntity.ok(liked ? "Liked" : "Unliked");
    }
}
