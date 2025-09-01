package com.instagramclone.controller;

import com.instagramclone.dto.FollowerDto;
import com.instagramclone.service.FollowerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/followers")
public class FollowerController {

    private final FollowerService followerService;

    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    // ✅ Follow / Unfollow or send follow request
    @PostMapping("/toggle/{followingUsername}")
    public ResponseEntity<String> toggleFollow(
            @PathVariable String followingUsername,
            @RequestParam String username
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: You can only follow/unfollow as the logged-in user.");
        }

        String result = followerService.toggleFollow(username, followingUsername);
        return ResponseEntity.ok(result);
    }
    
    
 // ✅ Check follow status (FOLLOWING, PENDING, FOLLOW)
    @GetMapping("/status/{targetUsername}")
    public ResponseEntity<Map<String, String>> getFollowStatus(
            @PathVariable String targetUsername,
            @RequestParam String currentUsername
    ) {
        String status = followerService.getFollowStatus(currentUsername, targetUsername);

        Map<String, String> response = new HashMap<>();
        response.put("status", status);

        return ResponseEntity.ok(response);
    }


    // ✅ Check if current user follows someone
    @GetMapping("/is-following/{targetUsername}")
    public ResponseEntity<Boolean> isFollowing(
            @PathVariable String targetUsername,
            @RequestParam String currentUsername
    ) {
        boolean isFollowing = followerService.isFollowing(currentUsername, targetUsername);
        return ResponseEntity.ok(isFollowing);
    }
    


    // ✅ Get list of followers
    @GetMapping("/followers/{username}")
    public ResponseEntity<List<FollowerDto>> getFollowers(@PathVariable String username) {
        return ResponseEntity.ok(followerService.getFollowers(username));
    }

    // ✅ Get list of following
    @GetMapping("/following/{username}")
    public ResponseEntity<List<FollowerDto>> getFollowing(@PathVariable String username) {
        return ResponseEntity.ok(followerService.getFollowing(username));
    }

    // ✅ Search followers
    @GetMapping("/search-followers")
    public ResponseEntity<List<FollowerDto>> searchFollowers(@RequestParam String username) {
        List<FollowerDto> followers = followerService.searchFollowersByUsername(username);
        return ResponseEntity.ok(followers);
    }

    // ✅ Search following
    @GetMapping("/search-following")
    public ResponseEntity<List<FollowerDto>> searchFollowing(@RequestParam String username) {
        List<FollowerDto> following = followerService.searchFollowingByUsername(username);
        return ResponseEntity.ok(following);
    }

    // ✅ Get pending follow requests (for private accounts)
    @GetMapping("/requests/pending/{username}")
    public ResponseEntity<List<FollowerDto>> getPendingRequests(@PathVariable String username) {
        return ResponseEntity.ok(followerService.getPendingRequests(username));
    }

    // ✅ Accept a follow request
    @PutMapping("/requests/{requestId}/accept")
    public ResponseEntity<String> acceptFollowRequest(@PathVariable Long requestId) {
        String result = followerService.acceptFollowRequest(requestId);
        return ResponseEntity.ok(result);
    }

    // ✅ Reject a follow request
    @DeleteMapping("/requests/{requestId}/reject")
    public ResponseEntity<String> rejectFollowRequest(@PathVariable Long requestId) {
        String result = followerService.rejectFollowRequest(requestId);
        return ResponseEntity.ok(result);
    }
}
