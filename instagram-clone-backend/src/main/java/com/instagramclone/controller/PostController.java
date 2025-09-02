package com.instagramclone.controller;

import com.instagramclone.dto.PostDTO;
import com.instagramclone.model.Post;
import com.instagramclone.model.User;
import com.instagramclone.service.PostService;
import com.instagramclone.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
            Principal principal) {
        try {
            // Ensure user is authenticated
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Post User not found: " + username));

            // Create new Post object
            Post post = new Post();
            post.setMediaData(mediaFile.getBytes());
            post.setMediaType(mediaType);
            post.setCaption(caption);
            post.setUser(user);
            post.setCreatedAt(new Date());

            // Handle optional audio file
            if (audioFile != null && !audioFile.isEmpty()) {
                post.setAudioName(audioName);
                post.setAudioData(audioFile.getBytes());
                post.setAudioType(audioFile.getContentType());
            }

            // Save post
            Post savedPost = postService.createPost(post);
            return ResponseEntity.ok(new PostDTO(savedPost, username));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing media files");
        }
    }

    // Get paginated posts
    @GetMapping("/all")
    public ResponseEntity<Page<PostDTO>> getAllPosts(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            String currentUsername = principal.getName(); // logged-in user

            Page<Post> postsPage = postService.getAllPosts(page, size);

            if (postsPage.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<PostDTO> postDTOs = postsPage.stream()
                    .map(post -> {
                        @SuppressWarnings("unused")
						boolean liked = false;
                        if (currentUsername != null && post.getLikes() != null) {
                            liked = post.getLikes().stream()
                                    .anyMatch(like -> like.getUser().getUsername().equals(currentUsername));
                        }
                        return new PostDTO(post, currentUsername);
                    })
                    .collect(Collectors.toList());

            Page<PostDTO> dtoPage = new PageImpl<>(postDTOs, PageRequest.of(page, size), postsPage.getTotalElements());
            return ResponseEntity.ok(dtoPage);

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
