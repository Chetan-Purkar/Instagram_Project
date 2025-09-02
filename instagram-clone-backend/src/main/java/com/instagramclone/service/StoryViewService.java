package com.instagramclone.service;

import com.instagramclone.dto.StoryViewDTO;
import com.instagramclone.model.Story;
import com.instagramclone.model.StoryView;
import com.instagramclone.model.User;
import com.instagramclone.repository.StoryRepository;
import com.instagramclone.repository.StoryViewRepository;
import com.instagramclone.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoryViewService {

    private final StoryViewRepository storyViewRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    public StoryViewService(StoryViewRepository storyViewRepository,
                            StoryRepository storyRepository,
                            UserRepository userRepository) {
        this.storyViewRepository = storyViewRepository;
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
    }

    /**
     * Add a view for a story by a user.
     * If the user has already viewed the story, returns the existing view.
     */
    public StoryViewDTO addView(Long storyId, String username) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        User viewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<StoryView> existingViews = storyViewRepository.findByStoryAndViewer(story, viewer);
        if (!existingViews.isEmpty()) {
            return mapToDTO(existingViews.get(0));
        }

        // ✅ Use constructor that auto-sets viewedAt
        StoryView storyView = new StoryView(story, viewer);
        StoryView savedView = storyViewRepository.save(storyView);
        return mapToDTO(savedView);
    }


    /**
     * Get all viewers for a story
     */
    public List<StoryViewDTO> getViewsByStory(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        return storyViewRepository.findByStory(story)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Count total views for a story
     */
    public long countViews(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        return storyViewRepository.countByStory(story);
    }

    /**
     * Map StoryView entity to StoryViewDTO
     */
    private StoryViewDTO mapToDTO(StoryView storyView) {
        return new StoryViewDTO(
                storyView.getId(),
                storyView.getStory().getId(),
                storyView.getViewer().getId(),
                storyView.getViewer().getUsername(),
                encodeProfileImage(storyView.getViewer().getProfileImage()),
                storyView.getViewedAt()
        );
    }

    /**
     * Convert profile image byte[] to Base64 string
     */
    private String encodeProfileImage(byte[] profileImage) {
        return (profileImage == null || profileImage.length == 0) ? null
                : java.util.Base64.getEncoder().encodeToString(profileImage);
    }
}
