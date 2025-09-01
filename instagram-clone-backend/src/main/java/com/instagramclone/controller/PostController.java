package com.instagramclone.controller;

import com.instagramclone.dto.PostDTO;
import com.instagramclone.model.Post;
import com.instagramclone.model.User;
import com.instagramclone.service.PostService;
import com.instagramclone.service.UserService;

import io.jsonwebtoken.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }
    
    @Autowired
    private UserService userService;

    // Create a new post
    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("mediaData") MultipartFile mediaFile,
            @RequestParam String mediaType,
            @RequestParam(value = "audioData", required = false) MultipartFile audioFile,
            @RequestParam String caption,
            @RequestParam String audioName,
            Principal principal) throws java.io.IOException {

        try {
            // Ensure user is authenticated
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException(" post User not found: " + username));

            // Create new Post object
            Post post = new Post();
            post.setMediaData(mediaFile.getBytes()); // ✅ IOException handled in catch block
            post.setMediaType(mediaType);
            post.setCaption(caption);
            post.setUser(user);
            post.setCreatedAt(new Date());

            // Handle optional audio file
            if (audioFile != null && !audioFile.isEmpty()) {
            	post.setAudioName(audioName);
                post.setAudioData(audioFile.getBytes()); // ✅ IOException handled in catch block
                post.setAudioType(audioFile.getContentType());
            }

            // Save post
            Post savedPost = postService.createPost(post);
            return ResponseEntity.ok(new PostDTO(savedPost, username));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing media files");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts(Principal principal) {
        try {
            String currentUsername = principal.getName(); // ✅ Logged-in user
            
            

            List<Post> posts = postService.getAllPosts();

            if (posts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<PostDTO> postDTOs = posts.stream()
                .filter(post -> post != null && post.getUser() != null)
                .map(post -> {
                    @SuppressWarnings("unused")
					boolean liked = false;

                    if (currentUsername != null && post.getLikes() != null) {
                        liked = post.getLikes().stream()
                                .anyMatch(like -> like.getUser().getUsername().equals(currentUsername));
                    }
            
                    return new PostDTO(post, currentUsername); // ✅ DTO includes liked status
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(postDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    // Delete a post by ID
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        try {
            boolean isDeleted = postService.deletePost(postId);
            if (isDeleted) {
                return ResponseEntity.ok("Post deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Post not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error deleting post.");
        }
    }
}
