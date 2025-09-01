package com.instagramclone.controller;

import com.instagramclone.dto.StoryLikeDTO;
import com.instagramclone.model.User;
import com.instagramclone.service.StoryLikeService;
import com.instagramclone.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories/likes")
public class StoryLikeController {

    private final StoryLikeService storyLikeService;
    private final UserService userService;

    public StoryLikeController(StoryLikeService storyLikeService, UserService userService) {
        this.storyLikeService = storyLikeService;
        this.userService = userService;
    }

    // ✅ Like a story (Authenticated User)
    @PostMapping("/{storyId}")
    public StoryLikeDTO likeStory(@PathVariable Long storyId,
                                  @AuthenticationPrincipal UserDetails principal) {
        String username = principal.getUsername();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return storyLikeService.likeStory(storyId, user.getId());
    }

    // ✅ Unlike a story (Authenticated User)
    @DeleteMapping("/{storyId}")
    public String unlikeStory(@PathVariable Long storyId,
                              @AuthenticationPrincipal UserDetails principal) {
        String username = principal.getUsername();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        storyLikeService.unlikeStory(storyId, user.getId());
        return "Unliked successfully";
    }

    // ✅ Get all likes for a story
    @GetMapping("/{storyId}")
    public List<StoryLikeDTO> getLikes(@PathVariable Long storyId) {
        return storyLikeService.getLikesForStory(storyId);
    }

    // ✅ Get like count
    @GetMapping("/{storyId}/count")
    public long getLikeCount(@PathVariable Long storyId) {
        return storyLikeService.countLikes(storyId);
    }
}
