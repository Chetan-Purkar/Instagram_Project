package com.instagramclone.controller;

import com.instagramclone.dto.PostDTO;
import com.instagramclone.dto.UserDTO;
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
    private final PostService postService; // Inject PostService

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

    // Get user by username along with their posts
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username, Principal principal ) {
        Optional<User> user = userService.findByUsername(username);
        String currentUsername = principal.getName(); // Logged-in user

        if (user.isPresent()) {
            User foundUser = user.get();
            
            // Convert User to UserDTO
            UserDTO userDTO = new UserDTO(foundUser);

            // Fetch posts for the user
            List<Post> posts = postService.getPostByUsername(username);
            List<PostDTO> postDTOs = posts.stream()
                    .filter(post -> post != null && post.getUser() != null)
                    .map(post -> new PostDTO(post, currentUsername)) // Pass currentUsername for liked status
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", userDTO.getId());
            response.put("username", userDTO.getUsername());
            response.put("name", userDTO.getName());
            response.put("email", userDTO.getEmail());
            response.put("bio", userDTO.getBio());
            response.put("profileImage", userDTO.getProfileImage());
            response.put("likedByCurrentUser", userDTO.isLikedByCurrentUser());
            response.put("posts", postDTOs);

            return ResponseEntity.ok(response);

            
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
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

}
