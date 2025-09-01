package com.instagramclone.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a like on a Story by a User.
 * A Story can have many likes, and a User can like many Stories.
 */
@Entity
@Table(name = "story_likes")
public class StoryLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The story that was liked */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    /** The user who liked the story */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Timestamp of when the like was created */
    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    // ---------- Constructors ----------

    public StoryLike() {
        this.likedAt = LocalDateTime.now(); // default timestamp
    }

    public StoryLike(Story story, User user) {
        this.story = story;
        this.user = user;
        this.likedAt = LocalDateTime.now();
    }

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(LocalDateTime likedAt) {
        this.likedAt = likedAt;
    }
}
