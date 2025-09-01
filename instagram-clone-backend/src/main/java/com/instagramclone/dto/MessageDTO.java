package com.instagramclone.dto;

import com.instagramclone.enums.MessageStatus;
import com.instagramclone.model.Message;
import com.instagramclone.model.Story;

import java.time.LocalDateTime;
import java.util.Base64;

public class MessageDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime timestamp;

    // ✅ Extra field for story reply
    private Long storyId;
    private String storyPreview;

    // ✅ Delivery status
    private MessageStatus status;

    // ✅ Seen info
    private LocalDateTime seenAt;

    // ✅ Security / soft delete
    private boolean deletedBySender;
    private boolean deletedByReceiver;
    
    
    /* ------------------- 📌 Constructors ------------------- */
    public MessageDTO() {}

    public MessageDTO(Long receiverId, String content, LocalDateTime timestamp) {
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // ✅ Constructor to convert from Entity
    public MessageDTO(Message message) {
        this.id = message.getId();
        this.senderId = message.getSender().getId();
        this.receiverId = message.getReceiver().getId();
        this.content = message.getContent();
        this.timestamp = message.getTimestamp();
        this.status = message.getStatus();

        this.seenAt = message.getSeenAt();
        this.deletedBySender = message.isDeletedBySender();
        this.deletedByReceiver = message.isDeletedByReceiver();

        // ✅ Add story info if available
        if (message.getStory() != null) {
            Story story = message.getStory();
            this.storyId = story.getId();

            // Prefer caption, else fallback to media preview
            if (story.getCaption() != null && !story.getCaption().isEmpty()) {
                this.storyPreview = story.getCaption();
            } else if (story.getMediaData() != null) {
                // ⚠️ Convert only first few bytes to Base64 for preview
                byte[] mediaData = story.getMediaData();
                int previewLength = Math.min(mediaData.length, 30);
                this.storyPreview = "Media Preview: " +
                        Base64.getEncoder().encodeToString(
                                java.util.Arrays.copyOf(mediaData, previewLength)
                        ) + "...";
            } else {
                this.storyPreview = "Story content";
            }
        }
    }

    // ✅ Alternative constructor for custom preview
    public MessageDTO(Message message, Long storyId, String storyPreview) {
        this(message); // reuse entity constructor
        this.storyId = storyId;
        this.storyPreview = storyPreview;
    }

    /* ------------------- 📌 Getters & Setters ------------------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public String getStoryPreview() { return storyPreview; }
    public void setStoryPreview(String storyPreview) { this.storyPreview = storyPreview; }

    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }

    public LocalDateTime getSeenAt() { return seenAt; }
    public void setSeenAt(LocalDateTime seenAt) { this.seenAt = seenAt; }

    public boolean isDeletedBySender() { return deletedBySender; }
    public void setDeletedBySender(boolean deletedBySender) { this.deletedBySender = deletedBySender; }

    public boolean isDeletedByReceiver() { return deletedByReceiver; }
    public void setDeletedByReceiver(boolean deletedByReceiver) { this.deletedByReceiver = deletedByReceiver; }

   
}
