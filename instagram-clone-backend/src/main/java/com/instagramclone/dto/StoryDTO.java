package com.instagramclone.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.instagramclone.enums.StoryPrivacy;

public class StoryDTO {
    private Long id;
    private String mediaData;   // Base64 image/video
    private String mediaType;   // e.g., image/png, video/mp4
    private String audioData;   // Base64 audio (optional)
    private String audioType;   // e.g., audio/mp3, audio/wav
    private String audioName;
    private String caption;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long userId;
    private String username;
    private String profileImage; // Base64 profile image

    private List<StoryLikeDTO> likes;
    private List<StoryReplyDTO> replies;
    private List<StoryViewDTO> views;
    private StoryPrivacy privacy;

    // ✅ Default constructor only
    public StoryDTO() {}

    // ---------- Getters & Setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMediaData() { return mediaData; }
    public void setMediaData(String mediaData) { this.mediaData = mediaData; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getAudioData() { return audioData; }
    public void setAudioData(String audioData) { this.audioData = audioData; }

    public String getAudioType() { return audioType; }
    public void setAudioType(String audioType) { this.audioType = audioType; }

    public String getAudioName() { return audioName; }
    public void setAudioName(String audioName) { this.audioName = audioName; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public List<StoryLikeDTO> getLikes() { return likes; }
    public void setLikes(List<StoryLikeDTO> likes) { this.likes = likes; }

    public List<StoryReplyDTO> getReplies() { return replies; }
    public void setReplies(List<StoryReplyDTO> replies) { this.replies = replies; }

    public List<StoryViewDTO> getViews() { return views; }
    public void setViews(List<StoryViewDTO> views) { this.views = views; }

    public StoryPrivacy getPrivacy() { return privacy; }
    public void setPrivacy(StoryPrivacy privacy) { this.privacy = privacy; }
}
