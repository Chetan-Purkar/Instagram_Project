package com.instagramclone.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.instagramclone.enums.StoryPrivacy;

@Entity
@Table(name = "stories")
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store image or video binary data
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] mediaData;

    // MIME type for media (e.g., image/png, video/mp4)
    private String mediaType;

    // Store audio binary data (optional, if a story has extra audio)
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] audioData;

    // MIME type for audio (e.g., audio/mp3, audio/wav)
    private String audioType;
    
    private String audioName;

    private String caption;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    // Story belongs to a User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Likes
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoryLike> likes = new ArrayList<>();

    // Views
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoryView> views = new ArrayList<>();

    // Replies
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoryReply> replies = new ArrayList<>();

    public Story() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = createdAt.plusHours(24);
    }

    @Enumerated(EnumType.STRING)
    private StoryPrivacy privacy = StoryPrivacy.PUBLIC; // default is PUBLIC

    // ---------- Getters & Setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getMediaData() {
        return mediaData;
    }

    public void setMediaData(byte[] mediaData) {
        this.mediaData = mediaData;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public byte[] getAudioData() {
        return audioData;
    }

    public void setAudioData(byte[] audioData) {
        this.audioData = audioData;
    }

    public String getAudioType() {
        return audioType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }
    

    public String getAudioName() {
		return audioName;
	}

	public void setAudioName(String audioName) {
		this.audioName = audioName;
	}

	public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<StoryLike> getLikes() {
        return likes;
    }

    public void setLikes(List<StoryLike> likes) {
        this.likes = likes;
    }

    public List<StoryView> getViews() {
        return views;
    }

    public void setViews(List<StoryView> views) {
        this.views = views;
    }

    public List<StoryReply> getReplies() {
        return replies;
    }

    public void setReplies(List<StoryReply> replies) {
        this.replies = replies;
    }

	public StoryPrivacy getPrivacy() {
		return privacy;
	}

	public void setPrivacy(StoryPrivacy privacy) {
		this.privacy = privacy;
	}
    
    
    
}
