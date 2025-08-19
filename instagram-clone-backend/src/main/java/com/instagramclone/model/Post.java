package com.instagramclone.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] mediaData; // Stores images & videos

    private String mediaType; // IMAGE, VIDEO, or AUDIO

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] audioData; // Stores audio files

    private String audioType; // MIME type (e.g., audio/mp3, audio/wav)

    private String caption;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // ✅ Prevents NullPointerException

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>(); // ✅ Prevents NullPointerException

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;
    
    

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
  


	// ✅ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public byte[] getMediaData() { return mediaData; }
    public void setMediaData(byte[] mediaData) { this.mediaData = mediaData; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public byte[] getAudioData() { return audioData; }
    public void setAudioData(byte[] audioData) { this.audioData = audioData; }

    public String getAudioType() { return audioType; }
    public void setAudioType(String audioType) { this.audioType = audioType; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { 
        this.comments = (comments != null) ? comments : new ArrayList<>(); // ✅ Prevent null issues
    }

    public List<Like> getLikes() { return likes; }
    public void setLikes(List<Like> likes) { 
        this.likes = (likes != null) ? likes : new ArrayList<>(); // ✅ Prevent null issues
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { 
        this.createdAt = (createdAt != null) ? createdAt : new Date(); // ✅ Prevent null issues
    }
}
