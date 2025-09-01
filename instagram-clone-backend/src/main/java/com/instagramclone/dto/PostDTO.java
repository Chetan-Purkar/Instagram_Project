package com.instagramclone.dto;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.instagramclone.model.Comment;
import com.instagramclone.model.Like;
import com.instagramclone.model.Post;

public class PostDTO {
    private Long id;
    private String username;
    private String profileImage;
    private String caption;

    private String mediaType;
    private byte[] mediaData;   // raw image/video bytes (optional, can be removed if you use only mediaUrl)
    private String mediaUrl;    // ✅ Base64-encoded image/video URL

    private String audioType;   // ✅ e.g. audio/mp3
    private byte[] audioData;   // raw audio bytes (optional, can be removed if you use only audioUrl)
    private String audioUrl;    // ✅ Base64-encoded audio URL
    private String audioName;

    private int likesCount;
    private int commentsCount;
    private Date createdAt;
    private List<String> comments;
    private boolean likedByCurrentUser;

    // ✅ Constructor for use in UserDTO (no currentUsername)
    public PostDTO(Post post) {
        this(post, null);
    }

    // ✅ Main constructor with currentUsername
    public PostDTO(Post post, String currentUsername) {
        this.id = post.getId();
        this.username = post.getUser().getUsername();

        // Profile image
        byte[] profileImageData = post.getUser().getProfileImage();
        this.profileImage = (profileImageData != null)
                ? Base64.getEncoder().encodeToString(profileImageData)
                : null;

        this.caption = post.getCaption();
        this.mediaType = post.getMediaType();

        // ✅ Media handling (Image/Video)
        if (post.getMediaData() != null) {
            this.mediaData = post.getMediaData();
            this.mediaUrl = "data:" + post.getMediaType() + ";base64,"
                    + Base64.getEncoder().encodeToString(post.getMediaData());
        }

        // ✅ Audio handling
        if (post.getAudioData() != null) {
            this.audioData = post.getAudioData();
            this.audioType = post.getAudioType();
            this.audioName = post.getAudioName();
            this.audioUrl = "data:" + post.getAudioType() + ";base64,"
                    + Base64.getEncoder().encodeToString(post.getAudioData());

            System.out.println("Generated Audio URL: " + this.audioUrl.substring(0, 30) + "...");
        }

        this.likesCount = (post.getLikes() != null) ? post.getLikes().size() : 0;
        this.commentsCount = (post.getComments() != null) ? post.getComments().size() : 0;
        this.createdAt = post.getCreatedAt();

        this.comments = (post.getComments() != null)
                ? post.getComments().stream().map(Comment::getText).collect(Collectors.toList())
                : null;

        if (currentUsername != null && post.getLikes() != null) {
            this.likedByCurrentUser = post.getLikes().stream()
                    .map(Like::getUser)
                    .anyMatch(user -> user.getUsername().equals(currentUsername));
        } else {
            this.likedByCurrentUser = false;
        }
    }

    // ✅ Getters & Setters (important for JSON serialization)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public byte[] getMediaData() { return mediaData; }
    public void setMediaData(byte[] mediaData) { this.mediaData = mediaData; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public byte[] getAudioData() { return audioData; }
    public void setAudioData(byte[] audioData) { this.audioData = audioData; }

    public String getAudioType() { return audioType; }
    public void setAudioType(String audioType) { this.audioType = audioType; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    
    public String getAudioName() { return audioName; }
    public void setAudioName(String audioName) { this.audioName = audioName; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<String> getComments() { return comments; }
    public void setComments(List<String> comments) { this.comments = comments; }

    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
}
