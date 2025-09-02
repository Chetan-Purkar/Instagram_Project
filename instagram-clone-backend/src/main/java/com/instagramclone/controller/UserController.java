package com.instagramclone.controller;

import com.instagramclone.dto.PostDTO;
import com.instagramclone.dto.UserDTO;
import com.instagramclone.enums.AccountPrivacy;
import com.instagramclone.model.Post;
import com.instagramclone.model.User;
import com.instagramclone.service.PostService;
import com.instagramclone.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    // Get the current logged-in user
    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = userDetails.getUsername();

        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(new UserDTO(user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Get user profile with paginated posts
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(
            @PathVariable String username,
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Optional<User> userOpt = userService.findByUsername(username);
        User viewer = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Viewer not found"));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        User targetUser = userOpt.get();
        boolean canViewFullProfile = userService.canViewProfile(targetUser, viewer);

        Map<String, Object> response = new HashMap<>();
        response.put("id", targetUser.getId());
        response.put("username", targetUser.getUsername());
        response.put("name", targetUser.getName());
        response.put("bio", targetUser.getBio());

        // Profile image as Base64
        byte[] profileImageData = targetUser.getProfileImage();
        String profileImage = (profileImageData != null) ?
                Base64.getEncoder().encodeToString(profileImageData) : null;
        response.put("profileImage", profileImage);

        if (canViewFullProfile) {
            // Fetch paginated posts
            Page<Post> postsPage = postService.getPostsByUsername(username, page, size);

            List<PostDTO> postDTOs = postsPage.stream()
                    .map(post -> new PostDTO(post, viewer.getUsername()))
                    .collect(Collectors.toList());

            response.put("posts", postDTOs);
            response.put("totalPosts", postsPage.getTotalElements());
            response.put("totalPages", postsPage.getTotalPages());
            response.put("currentPage", postsPage.getNumber());
        } else {
            // Not allowed → empty data
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
        List<String> usernames = userService.getAllUsers().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usernames);
    }

    // Get all users with details
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
            @RequestParam(required = false) MultipartFile profileImage) {
        try {
            Map<String, Object> userData = userService.updateUser(userId, name, email, bio, profileImage);
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Search users
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String username) {
        List<UserDTO> userDTOs = userService.searchUsers(username).stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // Update account privacy
    @PutMapping("/privacy")
    public ResponseEntity<String> updatePrivacy(@RequestParam AccountPrivacy privacy, Principal principal) {
        String currentUsername = principal.getName();
        userService.updatePrivacy(currentUsername, privacy);
        return ResponseEntity.ok("Privacy updated to " + privacy);
    }
}
