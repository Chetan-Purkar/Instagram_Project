package com.instagramclone.dto;

import java.time.LocalDateTime;


public class StoryReplyDTO {
    private Long id;             // Reply ID
    private Long storyId;        // The story being replied to
    private Long userId;         // User who replied
    private String username;     // Username of the replier
    private String profileImage; // Profile image of the replier story dto
    private String replyMessage;    // The reply message
    private LocalDateTime repliedAt; // When reply was made

    public StoryReplyDTO() {}

    public StoryReplyDTO(Long id, Long storyId, Long userId, String username, String profileImage,
                         String replyMessage, LocalDateTime repliedAt) {
        this.id = id;
        this.storyId = storyId;
        this.userId = userId;
        this.username = username;
        this.profileImage = profileImage;
        this.replyMessage = replyMessage;
        this.repliedAt = repliedAt;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getReplayMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public LocalDateTime getRepliedAt() {
        return repliedAt;
    }

    public void setRepliedAt(LocalDateTime repliedAt) {
        this.repliedAt = repliedAt;
    }
}
