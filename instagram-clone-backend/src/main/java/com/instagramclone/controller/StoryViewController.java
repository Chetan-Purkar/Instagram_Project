package com.instagramclone.controller;

import com.instagramclone.dto.StoryViewDTO;
import com.instagramclone.service.StoryViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/story-views")
public class StoryViewController {

    private final StoryViewService storyViewService;

    public StoryViewController(StoryViewService storyViewService) {
        this.storyViewService = storyViewService;
    }

    /**
     * Add a view for the authenticated user
     * @param storyId ID of the story being viewed
     * @param principal currently authenticated user
     * @return StoryViewDTO of the newly added or existing view
     */
    @PostMapping("/{storyId}")
    public ResponseEntity<StoryViewDTO> addView(@PathVariable Long storyId,
                                                @AuthenticationPrincipal UserDetails principal) {
        String username = principal.getUsername();
        StoryViewDTO viewDTO = storyViewService.addView(storyId, username);
        return ResponseEntity.ok(viewDTO);
    }

    /**
     * Get all viewers for a story
     * @param storyId ID of the story
     * @return List of StoryViewDTO representing all viewers
     */
    @GetMapping("/{storyId}/all")
    public ResponseEntity<List<StoryViewDTO>> getAllViews(@PathVariable Long storyId) {
        List<StoryViewDTO> views = storyViewService.getViewsByStory(storyId);
        return ResponseEntity.ok(views);
    }

    /**
     * Get total view count for a story
     * @param storyId ID of the story
     * @return total number of views
     */
    @GetMapping("/{storyId}/count")
    public ResponseEntity<Long> getViewCount(@PathVariable Long storyId) {
        long count = storyViewService.countViews(storyId);
        return ResponseEntity.ok(count);
    }
}
