package com.instagramclone.model;

import jakarta.persistence.*;

/**
 * Represents a "like" given by a user to a post.
 */
@Entity
@Table(name = "post_likes", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"post_id", "user_id"}) // Prevent duplicate likes
       })
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The post that was liked */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /** The user who liked the post */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Default constructor (JPA requirement) */
    public Like() {}

    /** Convenience constructor */
    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    // ---------------- Getters & Setters ---------------- //

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
