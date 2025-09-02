package com.instagramclone.dto;

import java.time.LocalDateTime;
import com.instagramclone.model.Notification;
import com.instagramclone.enums.NotificationType; // FOLLOW, FOLLOW_REQUEST, LIKE, COMMENT...

public class NotificationDTO {

    private Long id;
    private NotificationType type;        // ✅ Enum instead of String
    private String content;               
    private boolean isRead;               
    private boolean actionCompleted;      
    private UserDTO sender;               
    private LocalDateTime createdAt;      

    // Flattened related follower info (only used for follow-related notifications)
    private Long relatedFollowerId;       
    private String relatedFollowerStatus; // PENDING / ACCEPTED / REJECTED

    // ✅ Default constructor
    public NotificationDTO() {}

    // ✅ Full constructor
    public NotificationDTO(Long id, NotificationType type, String content, boolean isRead, boolean actionCompleted,
                           UserDTO sender, LocalDateTime createdAt,
                           Long relatedFollowerId, String relatedFollowerStatus) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.isRead = isRead;
        this.actionCompleted = actionCompleted;
        this.sender = sender;
        this.createdAt = createdAt;
        this.relatedFollowerId = relatedFollowerId;
        this.relatedFollowerStatus = relatedFollowerStatus;
    }

    // --- Getters & Setters ---
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

    public UserDTO getSender() { return sender; }
    public void setSender(UserDTO sender) { this.sender = sender; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getRelatedFollowerId() { return relatedFollowerId; }
    public void setRelatedFollowerId(Long relatedFollowerId) { this.relatedFollowerId = relatedFollowerId; }

    public String getRelatedFollowerStatus() { return relatedFollowerStatus; }
    public void setRelatedFollowerStatus(String relatedFollowerStatus) { this.relatedFollowerStatus = relatedFollowerStatus; }

    // ✅ Convenience method
    public boolean isFollowRequest() {
        return type == NotificationType.FOLLOW_REQUEST;
    }

    // ✅ Convert Notification entity → DTO safely
    public static NotificationDTO fromEntity(Notification notification) {
        UserDTO senderDto = new UserDTO(notification.getSender());

        Long followerId = null;
        String followerStatus = null;
        if (notification.getRelatedFollower() != null) {
            followerId = notification.getRelatedFollower().getId();
            followerStatus = notification.getRelatedFollower().getStatus() != null
                    ? notification.getRelatedFollower().getStatus().name()
                    : null;
        }

        return new NotificationDTO(
                notification.getId(),
                notification.getType(),
                notification.getContent(),
                notification.isRead(),
                notification.isActionCompleted(),
                senderDto,
                notification.getCreatedAt(),
                followerId,
                followerStatus
        );
    }
}
