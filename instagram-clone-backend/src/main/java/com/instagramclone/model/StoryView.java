package com.instagramclone.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "story_views")
public class StoryView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Many views belong to one story */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    /** A user can view many stories */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viewer_id", nullable = false) // renamed column for clarity
    private User viewer;

    @Column(name = "viewed_at", nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    /** ---------- Constructors ---------- */
    public StoryView() {
        // Default constructor required by JPA
    }

    public StoryView(Story story, User viewer) {
        this.story = story;
        this.viewer = viewer;
        this.viewedAt = LocalDateTime.now(); // auto-set when created
    }

    /** ---------- Getters & Setters ---------- */
    public Long getId() {
        return id;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public User getViewer() {
        return viewer;
    }

    public void setViewer(User viewer) {
        this.viewer = viewer;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    // No setter for viewedAt → keeps it immutable
}
