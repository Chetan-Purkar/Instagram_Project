package com.instagramclone.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.instagramclone.enums.FollowStatus;

@Entity
@Table(
    name = "followers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "following_id"})
    }
)
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who is following
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // The user who is being followed
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Column(nullable = false, updatable = false)
    private LocalDateTime followedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FollowStatus status;

    /** Default constructor */
    public Follower() {
        this.followedAt = LocalDateTime.now();
        this.status = FollowStatus.ACCEPTED; // Default for public accounts
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(User following) {
        this.following = following;
    }

    public LocalDateTime getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(LocalDateTime followedAt) {
        this.followedAt = followedAt;
    }

    public FollowStatus getStatus() {
        return status;
    }

	public void setStatus(FollowStatus status) {
		this.status = status;
	}


   
}
