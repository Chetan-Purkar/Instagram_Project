package com.instagramclone.controller;

import com.instagramclone.dto.StoryViewDTO;
import com.instagramclone.service.StoryViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
     */
    @PostMapping("/{storyId}")
    public ResponseEntity<StoryViewDTO> addView(@PathVariable Long storyId, Principal principal) {
        String username = principal.getName(); // authenticated username
        StoryViewDTO viewDTO = storyViewService.addView(storyId, username);
        return ResponseEntity.ok(viewDTO);
    }

    /**
     * Get all views (viewers) for a story
     */
    @GetMapping("/{storyId}/all")
    public ResponseEntity<List<StoryViewDTO>> getAllViews(@PathVariable Long storyId) {
        List<StoryViewDTO> views = storyViewService.getViewsByStory(storyId);
        return ResponseEntity.ok(views);
    }

    /**
     * Get total view count for a story
     */
    @GetMapping("/{storyId}/count")
    public ResponseEntity<Long> getViewCount(@PathVariable Long storyId) {
        long count = storyViewService.countViews(storyId);
        return ResponseEntity.ok(count);
    }
}
