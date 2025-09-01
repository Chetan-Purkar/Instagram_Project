	package com.instagramclone.controller;

import com.instagramclone.dto.PostDTO;
import com.instagramclone.dto.UserDTO;
import com.instagramclone.enums.AccountPrivacy;
import com.instagramclone.model.Post;
import com.instagramclone.model.User;
import com.instagramclone.service.PostService;
import com.instagramclone.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService; // Initialize PostService
    }

    // Get the current logged-in user
    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = userDetails.getUsername();
       
        return userService.findByUsername(username)
                .map(user -> {
                    UserDTO userDTO = new UserDTO(user);
                    return ResponseEntity.ok(userDTO);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username, Principal principal) {
        Optional<User> userOpt = userService.findByUsername(username);
        User viewer = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Viewer not found"));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        User targetUser = userOpt.get();

        boolean canViewFullProfile = userService.canViewProfile(targetUser, viewer);

        // Basic profile info visible to everyone (name, bio, profile image)
        Map<String, Object> response = new HashMap<>();
        response.put("id", targetUser.getId());
        response.put("username", targetUser.getUsername());
        response.put("name", targetUser.getName());
        response.put("bio", targetUser.getBio());

        // Profile image as Base64
        byte[] profileImageData = targetUser.getProfileImage();
        String profileImage = (profileImageData != null) ? 
                java.util.Base64.getEncoder().encodeToString(profileImageData) : null;
        response.put("profileImage", profileImage);

        if (canViewFullProfile) {
            // ✅ Only followers or self can see posts
            List<Post> posts = postService.getPostByUsername(username);
            List<PostDTO> postDTOs = posts.stream()
                    .map(post -> new PostDTO(post, viewer.getUsername()))
                    .collect(Collectors.toList());
            response.put("posts", postDTOs);

            // You can also add followers, following, stories here if needed
            // e.g., response.put("followers", followerService.getFollowers(username));
            // e.g., response.put("following", followerService.getFollowing(username));
        } else {
            // ❌ Not a follower → cannot see posts/stories/followers/following
            response.put("posts", Collections.emptyList());
            response.put("followers", Collections.emptyList());
            response.put("following", Collections.emptyList());
            response.put("stories", Collections.emptyList());
        }

        return ResponseEntity.ok(response);
    }




    // Get all usernames
    @GetMapping("/allusers")
    public ResponseEntity<List<String>> getAllUsernames() {
        List<User> users = userService.getAllUsers();

        List<String> usernames = users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        return ResponseEntity.ok(usernames);
    }

    // Get all users (with their details)
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Update user details
    @PutMapping("/{userId}/update")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String bio,
            @RequestParam(required = false) MultipartFile profileImage) throws java.io.IOException {

        try {
            Map<String, Object> userData = userService.updateUser(userId, name, email, bio, profileImage);
            return ResponseEntity.ok(userData);
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest().body("Error processing profile image");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String username) {
        List<User> users = userService.searchUsers(username);
        List<UserDTO> userDTOs = users.stream()
            .map(UserDTO::new)  // Using the constructor without likedByCurrentUser
            .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }
    
    @PutMapping("/privacy")
    public ResponseEntity<String> updatePrivacy(@RequestParam AccountPrivacy privacy, Principal principal) {
        String currentUsername = principal.getName(); // logged-in user only
        userService.updatePrivacy(currentUsername, privacy);
        return ResponseEntity.ok("Privacy updated to " + privacy);
    }


}
