package com.instagramclone.dto;

import java.time.LocalDateTime;

public class StoryViewDTO {
    private Long id;             // View ID
    private Long storyId;        // The story being viewed
    private Long viewerId;       // User who viewed
    private String username;     // Username of the viewer
    private String profileImage; // Profile image of the viewer
    private LocalDateTime viewedAt; // When the story was viewed

    public StoryViewDTO() {}

    public StoryViewDTO(Long id, Long storyId, Long viewerId, String username, String profileImage, LocalDateTime viewedAt) {
        this.id = id;
        this.storyId = storyId;
        this.viewerId = viewerId;
        this.username = username;
        this.profileImage = profileImage;
        this.viewedAt = viewedAt;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public Long getViewerId() {
        return viewerId;
    }

    public void setViewerId(Long viewerId) {
        this.viewerId = viewerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
}
