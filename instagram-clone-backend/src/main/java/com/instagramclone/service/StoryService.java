package com.instagramclone.service;

import com.instagramclone.dto.StoryDTO;
import com.instagramclone.enums.AccountPrivacy;
import com.instagramclone.enums.FollowStatus;
import com.instagramclone.model.Follower;
import com.instagramclone.model.Story;
import com.instagramclone.model.User;
import com.instagramclone.repository.FollowerRepository;
import com.instagramclone.repository.StoryRepository;
import com.instagramclone.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoryService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final UserService userService;
    private final StoryViewService storyViewService;

    public StoryService(FollowerRepository followerRepository, UserRepository userRepository,
                        StoryRepository storyRepository, UserService userService,
                        StoryViewService storyViewService) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.userService = userService;
        this.storyViewService = storyViewService;
    }


    // --- Create Story ---
    public StoryDTO createStory(Story story) {
        if (story.getCreatedAt() == null) story.setCreatedAt(LocalDateTime.now());
        if (story.getExpiresAt() == null) story.setExpiresAt(LocalDateTime.now().plusHours(24));

        Story savedStory = storyRepository.save(story);
        return mapToDTO(savedStory, story.getUser().getId());
    }

    // --- Get active stories of a user ---
    public List<StoryDTO> getActiveStoriesByUser(String username, Long currentUserId) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return storyRepository.findByUserAndExpiresAtAfterOrderByCreatedAtDesc(user, LocalDateTime.now())
                .stream()
                .map(story -> mapToDTO(story, currentUserId))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

 // --- Get active stories of following users (public + accepted private) ---
    public List<StoryDTO> getStoriesOfFollowingUsers(String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all users that the current user follows (accepted)
        List<User> followingUsers = followerRepository.findByFollower(currentUser)
                .stream()
                .filter(f -> f.getStatus() == FollowStatus.ACCEPTED)
                .map(Follower::getFollowing)
                .collect(Collectors.toList());

        // Also include all public users among them (just in case)
        List<Story> stories = storyRepository.findByUserInAndExpiresAtAfter(followingUsers, LocalDateTime.now());

        return stories.stream()
                .map(story -> {
                    User owner = story.getUser();
                    // ✅ Allow if public or if current user follows them
                    if (owner.getAccountPrivacy() == AccountPrivacy.PUBLIC 
                        || followingUsers.contains(owner)
                        || owner.getId().equals(currentUser.getId())) {
                        return mapToDTO(story, currentUser.getId());
                    }
                    return null; // skip private accounts not followed
                })
                .filter(dto -> dto != null)
                .sorted((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt())) // newest first
                .collect(Collectors.toList());
    }


    // --- Delete expired stories ---
    public void deleteExpiredStories() {
        storyRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    // --- Mapper ---
    private StoryDTO mapToDTO(Story story, Long currentUserId) {
        User user = story.getUser();

        // Privacy check
        if (user.getAccountPrivacy() == AccountPrivacy.PRIVATE && currentUserId != null) {
            boolean isOwner = user.getId().equals(currentUserId);
            boolean isFollower = user.getFollowers().stream()
                    .anyMatch(f -> f.getFollower().getId().equals(currentUserId)); // fixed

            if (!isOwner && !isFollower) return null;
        }

        StoryDTO dto = new StoryDTO();
        dto.setId(story.getId());
        dto.setMediaData(toBase64(story.getMediaData()));
        dto.setMediaType(story.getMediaType());
        dto.setAudioData(toBase64(story.getAudioData()));
        dto.setAudioType(story.getAudioType());
        dto.setAudioName(story.getAudioName());
        dto.setCaption(story.getCaption());
        dto.setCreatedAt(story.getCreatedAt());
        dto.setExpiresAt(story.getExpiresAt());
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setProfileImage(toBase64(user.getProfileImage()));
        dto.setPrivacy(story.getPrivacy());

        dto.setViews(storyViewService.getViewsByStory(story.getId()));
        
        return dto;
    }

    private String toBase64(byte[] data) {
        return (data == null || data.length == 0) ? null : Base64.getEncoder().encodeToString(data);
    }
}
