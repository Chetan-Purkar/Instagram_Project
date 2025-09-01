package com.instagramclone.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a Post created by a user in the Instagram clone.
 * A Post may contain media (image/video), optional audio, a caption,
 * and is associated with comments and likes.
 */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stores image/video data */
    @Lob
    @Column(name = "media_data", columnDefinition = "LONGBLOB")
    private byte[] mediaData;

    /** Type of media: IMAGE, VIDEO */
    @Column(name = "media_type", length = 20)
    private String mediaType;

    /** Stores audio data if attached */
    @Lob
    @Column(name = "audio_data", columnDefinition = "LONGBLOB")
    private byte[] audioData;

    /** Original filename of audio */
    @Column(name = "audio_name")
    private String audioName;

    /** MIME type of audio (e.g., audio/mp3) */
    @Column(name = "audio_type", length = 50)
    private String audioType;

    /** Caption text */
    @Column(length = 500)
    private String caption;

    /** User who created the post */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Comments on this post */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    /** Likes on this post */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    /** Timestamp when the post was created */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    // ---------- Lifecycle Callbacks ----------

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

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

    public String getAudioName() {
        return audioName;
    }
    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getAudioType() {
        return audioType;
    }
    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }
    public void setComments(List<Comment> comments) {
        this.comments = (comments != null) ? comments : new ArrayList<>();
    }

    public List<Like> getLikes() {
        return likes;
    }
    public void setLikes(List<Like> likes) {
        this.likes = (likes != null) ? likes : new ArrayList<>();
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = (createdAt != null) ? createdAt : new Date();
    }
}
