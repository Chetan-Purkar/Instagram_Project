package com.instagramclone.controller;

import com.instagramclone.dto.FollowerDto;
import com.instagramclone.service.FollowerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/followers")
public class FollowerController {

    private final FollowerService followerService;

    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @PostMapping("/toggle/{followingUsername}")
    public ResponseEntity<String> toggleFollow(
            @PathVariable String followingUsername,
            @RequestParam String username
    ) {
        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: You can only follow/unfollow as the logged-in user.");
        }

        String result = followerService.toggleFollow(username, followingUsername);
        return ResponseEntity.ok(result);
    }
    
    
    @GetMapping("/is-following/{targetUsername}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable String targetUsername, @RequestParam String currentUsername) {
        boolean isFollowing = followerService.isFollowing(currentUsername, targetUsername);
        return ResponseEntity.ok(isFollowing);
    }


    @GetMapping("/followers/{username}")
    public ResponseEntity<List<FollowerDto>> getFollowers(@PathVariable String username) {
        return ResponseEntity.ok(followerService.getFollowers(username));
    }

    @GetMapping("/following/{username}")
    public ResponseEntity<List<FollowerDto>> getFollowing(@PathVariable String username) {
        return ResponseEntity.ok(followerService.getFollowing(username));
    }
    

    @GetMapping("/search-followers")
    public ResponseEntity<List<FollowerDto>> searchFollowers(@RequestParam String username) {
        List<FollowerDto> followers = followerService.searchFollowersByUsername(username);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/search-following")
    public ResponseEntity<List<FollowerDto>> searchFollowing(@RequestParam String username) {
        List<FollowerDto> following = followerService.searchFollowingByUsername(username);
        return ResponseEntity.ok(following);
    }


}
