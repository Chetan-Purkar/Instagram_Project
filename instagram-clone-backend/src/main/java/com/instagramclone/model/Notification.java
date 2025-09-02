package com.instagramclone.model;

import com.instagramclone.enums.NotificationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type; // FOLLOW_REQUEST, LIKE, COMMENT, etc.

    @Column(nullable = false, length = 255)
    private String content; // e.g., "John sent you a follow request"

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private boolean actionCompleted = false; 
    // for follow request: true if accepted/rejected

    /* ------------------- RELATIONSHIPS ------------------- */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // user who triggered notification

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // user who receives notification

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_follower_id", foreignKey = @ForeignKey(name = "fk_notification_follower"))
    @OnDelete(action = OnDeleteAction.CASCADE)  // Hibernate annotation
    private Follower relatedFollower;

    // Only used when type == FOLLOW_REQUEST

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* ------------------- CONSTRUCTORS ------------------- */

    public Notification() {}

    public Notification(NotificationType type, String content, User sender, User receiver) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }

    /* ------------------- JPA LIFECYCLE ------------------- */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /* ------------------- GETTERS & SETTERS ------------------- */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isActionCompleted() { return actionCompleted; }
    public void setActionCompleted(boolean actionCompleted) { this.actionCompleted = actionCompleted; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public Follower getRelatedFollower() { return relatedFollower; }
    public void setRelatedFollower(Follower relatedFollower) { this.relatedFollower = relatedFollower; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
