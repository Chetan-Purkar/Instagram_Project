package com.instagramclone.controller;

import com.instagramclone.dto.StoryDTO;
import com.instagramclone.enums.StoryPrivacy;
import com.instagramclone.model.Story;
import com.instagramclone.model.User;
import com.instagramclone.service.StoryService;
import com.instagramclone.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    @Autowired
    private StoryService storyService;
    
    @Autowired
    private UserService userService;

    /**
     * Create a story for the authenticated user
     */
    @PostMapping("/create")
    public ResponseEntity<?> createStory(
            @RequestParam("mediaFile") MultipartFile mediaFile,
            @RequestParam(required = false) MultipartFile audioFile,
            @RequestParam(required = false) String caption,
            @RequestParam(defaultValue = "24") int durationInHours,
            @RequestParam(defaultValue = "PUBLIC") StoryPrivacy privacy, 
            Principal principal) {

        try {
            // Get authenticated user
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // Create Story object
            Story story = new Story();
            story.setMediaData(mediaFile.getBytes());
            story.setMediaType(mediaFile.getContentType());
            story.setCaption(caption);
            story.setUser(user);
            story.setCreatedAt(LocalDateTime.now());
            story.setExpiresAt(LocalDateTime.now().plusHours(durationInHours));
            story.setPrivacy(privacy);

            if (audioFile != null && !audioFile.isEmpty()) {
                story.setAudioData(audioFile.getBytes());
                story.setAudioType(audioFile.getContentType());
            }

            // Save and return DTO
            StoryDTO storyDTO = storyService.createStory(story);
            return ResponseEntity.ok(storyDTO);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing media or audio files");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Get all active stories (global)
     */
    @GetMapping("/all")
    public ResponseEntity<List<StoryDTO>> getAllStories() {
        List<StoryDTO> stories = storyService.getAllActiveStories();
        return ResponseEntity.ok(stories);
    }


	/**
     * Get active stories of the authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<List<StoryDTO>> getMyStories(@AuthenticationPrincipal UserDetails principal) {
        List<StoryDTO> stories = storyService.getActiveStoriesByUser(principal.getUsername());
        return ResponseEntity.ok(stories);
    }

    /**
     * Get active stories of a specific user by username
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<StoryDTO>> getUserStories(@PathVariable String username) {
        List<StoryDTO> stories = storyService.getActiveStoriesByUser(username);
        return ResponseEntity.ok(stories);
    }

   

    /**
     * Get active stories of users followed by the authenticated user
     */
    @GetMapping("/following")
    public ResponseEntity<List<StoryDTO>> getFollowingStories(@AuthenticationPrincipal UserDetails principal) {
        List<StoryDTO> stories = storyService.getStoriesOfFollowingUsers(principal.getUsername());
        return ResponseEntity.ok(stories);
    }

    /**
     * Cleanup expired stories
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanupExpiredStories() {
        storyService.deleteExpiredStories();
        return ResponseEntity.ok("Expired stories deleted successfully.");
    }
}
