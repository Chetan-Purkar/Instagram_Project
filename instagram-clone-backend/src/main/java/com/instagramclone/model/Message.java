package com.instagramclone.model;

import com.instagramclone.enums.MessageStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages",
       indexes = {
           @Index(name = "idx_sender_receiver", columnList = "sender_id, receiver_id"),
           @Index(name = "idx_timestamp", columnList = "timestamp")
       }
)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ------------------- 📌 Sender & Receiver ------------------- */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /* ------------------- 📌 Message Content ------------------- */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // Reply to a story
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    /* ------------------- 📌 Status & Security ------------------- */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.SENT;  // SENT, DELIVERED, SEEN

    // Exact seen time (useful for read receipts)
    private LocalDateTime seenAt;

    // Soft delete (like Instagram "unsend" but with server tracking)
    private boolean deletedBySender = false;
    private boolean deletedByReceiver = false;


    /* ------------------- 📌 Lifecycle ------------------- */
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    /* ------------------- 📌 Getters & Setters ------------------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Story getStory() { return story; }
    public void setStory(Story story) { this.story = story; }

    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }

    public LocalDateTime getSeenAt() { return seenAt; }
    public void setSeenAt(LocalDateTime seenAt) { this.seenAt = seenAt; }

    public boolean isDeletedBySender() { return deletedBySender; }
    public void setDeletedBySender(boolean deletedBySender) { this.deletedBySender = deletedBySender; }

    public boolean isDeletedByReceiver() { return deletedByReceiver; }
    public void setDeletedByReceiver(boolean deletedByReceiver) { this.deletedByReceiver = deletedByReceiver; }

}
