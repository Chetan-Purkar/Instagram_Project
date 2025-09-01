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

    public StoryViewService(StoryViewRepository storyViewRepository, StoryRepository storyRepository, UserRepository userRepository) {
        this.storyViewRepository = storyViewRepository;
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
    }

    public StoryViewDTO addView(Long storyId, String username) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        User viewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Fetch all existing views by this user for the story
        List<StoryView> existingViews = storyViewRepository.findByStoryAndViewer(story, viewer);

        if (!existingViews.isEmpty()) {
            // Return first existing view (ignore duplicates)
            return mapToDTO(existingViews.get(0));
        }

        // Save new view
        StoryView storyView = new StoryView();
        storyView.setStory(story);
        storyView.setViewer(viewer);

        StoryView savedView = storyViewRepository.save(storyView);
        return mapToDTO(savedView);
    }

    
    // ✅ Get all viewers for a story
    public List<StoryViewDTO> getViewsByStory(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        List<StoryView> views = storyViewRepository.findByStory(story);

        return views.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Count views for a story
    public long countViews(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        return storyViewRepository.countByStory(story);
    }

    // 🔄 Mapper: StoryView → StoryViewDTO
    private StoryViewDTO mapToDTO(StoryView storyView) {
        return new StoryViewDTO(
                storyView.getId(),
                storyView.getStory().getId(),
                storyView.getViewer().getId(),
                storyView.getViewer().getUsername(),
                encodeProfileImage(storyView.getViewer().getProfileImage()), // convert byte[] to Base64 String
                storyView.getViewedAt()
        );
    }

    // 🖼 Convert profile image (byte[]) to Base64 String
    private String encodeProfileImage(byte[] profileImage) {
        if (profileImage != null) {
            return java.util.Base64.getEncoder().encodeToString(profileImage);
        }
        return null;
    }

	
}
