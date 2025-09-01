package com.instagramclone.service;

import com.instagramclone.dto.StoryDTO;
import com.instagramclone.enums.AccountPrivacy;
import com.instagramclone.model.Story;
import com.instagramclone.model.User;
import com.instagramclone.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoryService {

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private UserService userService;

    /**
     * Create a new story for the authenticated user
     */
    public StoryDTO createStory(Story story) {

        // Set timestamps if not already set
        if (story.getCreatedAt() == null) {
            story.setCreatedAt(LocalDateTime.now());
        }
        if (story.getExpiresAt() == null) {
            story.setExpiresAt(LocalDateTime.now().plusHours(24));
        }

        // Save the story
        Story savedStory = storyRepository.save(story);

        // Convert to DTO and return
        return mapToDTO(savedStory, story.getUser().getId()); // passing owner ID as currentUserId
    }

    /**
     * Get all active stories of a specific user
     */
    public List<StoryDTO> getActiveStoriesByUser(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return storyRepository.findByUserAndExpiresAtAfterOrderByCreatedAtDesc(user, LocalDateTime.now())
                .stream()
                .map(story -> mapToDTO(story, user.getId()))
                .filter(dto -> dto != null) // remove nulls from privacy check
                .collect(Collectors.toList());
    }

    /**
     * Get all active stories globally
     */
    public List<StoryDTO> getAllActiveStories() {
        return storyRepository.findByExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime.now())
                .stream()
                .map(story -> mapToDTO(story, null)) // null means guest / no login
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Get active stories of users followed by the given user
     */
    public List<StoryDTO> getStoriesOfFollowingUsers(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return storyRepository.findStoriesOfFollowingUsers(user.getId(), LocalDateTime.now())
                .stream()
                .map(story -> mapToDTO(story, user.getId()))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Delete expired stories
     */
    public void deleteExpiredStories() {
        storyRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    /**
     * Map Story entity -> DTO
     */
    private StoryDTO mapToDTO(Story story, Long currentUserId) {
        User user = story.getUser();

        // ✅ Privacy check
        if (user.getAccountPrivacy() == AccountPrivacy.PRIVATE && currentUserId != null) {
            boolean isOwner = user.getId().equals(currentUserId);
            boolean isFollower = user.getFollowers().stream()
                    .anyMatch(f -> f.getId().equals(currentUserId));

            if (!isOwner && !isFollower) {
                return null; // 🚫 Not allowed
            }
        }

        String profileImageBase64 = toBase64(user.getProfileImage());
        String mediaBase64 = toBase64(story.getMediaData());
        String audioBase64 = toBase64(story.getAudioData());

        // ✅ Populate DTO with setters
        StoryDTO dto = new StoryDTO();
        dto.setId(story.getId());
        dto.setMediaData(mediaBase64);
        dto.setMediaType(story.getMediaType());
        dto.setAudioData(audioBase64);
        dto.setAudioType(story.getAudioType());
        dto.setAudioName(story.getAudioName());
        dto.setCaption(story.getCaption());
        dto.setCreatedAt(story.getCreatedAt());
        dto.setExpiresAt(story.getExpiresAt());
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setProfileImage(profileImageBase64);

        return dto;
    }

    private String toBase64(byte[] data) {
        return (data == null || data.length == 0) ? null : Base64.getEncoder().encodeToString(data);
    }
}
