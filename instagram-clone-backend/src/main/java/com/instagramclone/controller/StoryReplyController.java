package com.instagramclone.controller;

import com.instagramclone.dto.StoryReplyDTO;
import com.instagramclone.service.StoryReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/story-replies")
public class StoryReplyController {

    @Autowired
    private StoryReplyService storyReplyService;

    /**
     * Add a reply to a story
     */
    @PostMapping("/{storyId}/add")
    public StoryReplyDTO addReply(
            @PathVariable Long storyId,
            @RequestParam String replyMessage,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        return storyReplyService.addReply(storyId, replyMessage, username);
    }

    /**
     * Get all replies for a story
     */
    @GetMapping("/{storyId}")
    public List<StoryReplyDTO> getRepliesByStory(@PathVariable Long storyId) {
        return storyReplyService.getRepliesByStory(storyId);
    }

    /**
     * Get all replies made by authenticated user
     */
    @GetMapping("/my-replies")
    public List<StoryReplyDTO> getRepliesByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return storyReplyService.getRepliesByUser(username);
    }

    /**
     * Count replies for a story
     */
    @GetMapping("/{storyId}/count")
    public long countReplies(@PathVariable Long storyId) {
        return storyReplyService.countRepliesByStory(storyId);
    }
}
