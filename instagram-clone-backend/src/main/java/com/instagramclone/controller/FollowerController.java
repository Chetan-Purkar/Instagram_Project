package com.instagramclone.controller;

import com.instagramclone.dto.FollowerDto;
import com.instagramclone.model.Follower;
import com.instagramclone.service.FollowerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    @PostMapping("/toggle/{username}")
    public ResponseEntity<Map<String, String>> toggleFollow(@PathVariable("username") String followingUsername,
                                                            Principal principal) {
        String followerUsername = principal.getName();

        if (followerUsername.equals(followingUsername)) {
            return ResponseEntity.badRequest().body(Map.of("error", "❌ You cannot follow yourself controller"));
        }

        String result = followerService.toggleFollow(followerUsername, followingUsername);
        return ResponseEntity.ok(Map.of("message", result));
    }


    // ✅ Check follow status (FOLLOWING, PENDING, FOLLOW)
    @GetMapping("/status/{targetUsername}")
    public ResponseEntity<Map<String, String>> getFollowStatus(
            @PathVariable String targetUsername,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: Please log in."));
        }

        String currentUsername = principal.getName();
        String status = followerService.getFollowStatus(currentUsername, targetUsername);

        return ResponseEntity.ok(Map.of("status", status));
    }

    // ✅ Check if current user follows someone
    @GetMapping("/is-following/{targetUsername}")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @PathVariable String targetUsername,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", false));
        }

        String currentUsername = principal.getName();
        boolean isFollowing = followerService.isFollowing(currentUsername, targetUsername);

        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }

    // ✅ Get list of followers
    @GetMapping("/{username}/followers")
    public ResponseEntity<List<FollowerDto>> getFollowers(@PathVariable String username) {
        return ResponseEntity.ok(followerService.getFollowers(username));
    }

    // ✅ Get list of following
    @GetMapping("/{username}/following")
    public ResponseEntity<List<FollowerDto>> getFollowing(@PathVariable String username) {
        return ResponseEntity.ok(followerService.getFollowing(username));
    }

    // ✅ Search followers
    @GetMapping("/search-followers")
    public ResponseEntity<List<FollowerDto>> searchFollowers(@RequestParam String username) {
        return ResponseEntity.ok(followerService.searchFollowersByUsername(username));
    }

    // ✅ Search following
    @GetMapping("/search-following")
    public ResponseEntity<List<FollowerDto>> searchFollowing(@RequestParam String username) {
        return ResponseEntity.ok(followerService.searchFollowingByUsername(username));
    }

    // ✅ Get pending follow requests (for private accounts)
    @GetMapping("/{username}/requests/pending")
    public ResponseEntity<List<FollowerDto>> getPendingRequests(@PathVariable String username) {
        return ResponseEntity.ok(followerService.getPendingRequests(username));
    }

    // ✅ Accept a follow request
    @PutMapping("/requests/{requestId}/accept")
    public ResponseEntity<Map<String, String>> acceptFollowRequest(
            @PathVariable Long requestId,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: Please log in."));
        }

        Follower request = followerService.getFollowerById(requestId);

        if (!request.getFollowing().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You cannot accept this request"));
        }

        String result = followerService.acceptFollowRequest(requestId);

        return ResponseEntity.ok(Map.of("message", result));
    }

    // ✅ Reject a follow request
    @PutMapping("/requests/{requestId}/reject")
    public ResponseEntity<Map<String, String>> rejectFollowRequest(
            @PathVariable Long requestId,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: Please log in."));
        }

        Follower request = followerService.getFollowerById(requestId);

        if (!request.getFollowing().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You cannot reject this request"));
        }

        String result = followerService.rejectFollowRequest(requestId);

        return ResponseEntity.ok(Map.of("message", result));
    }
}
