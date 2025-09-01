package com.instagramclone.dto;

import java.time.LocalDateTime;

public class StoryLikeDTO {
    private Long id;
    private Long storyId;
    private Long userId;
    private String username;
    private byte[] profileImage;
    private LocalDateTime likedAt;

    public StoryLikeDTO() {}

    
    public StoryLikeDTO(Long id, Long storyId, Long userId, String username, byte[] bs, LocalDateTime likedAt) {
        this.id = id;
        this.storyId = storyId;
        this.userId = userId;
        this.username = username;
        this.profileImage = bs;
        this.likedAt = likedAt;
    }

    // Getters and Setters
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

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public LocalDateTime getLikedAt() {
        return likedAt;
    }
    public void setLikedAt(LocalDateTime likedAt) {
        this.likedAt = likedAt;
    }
}
