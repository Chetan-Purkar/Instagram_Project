package com.instagramclone.service;

import com.instagramclone.dto.NotificationDTO;
import com.instagramclone.dto.UserDTO;
import com.instagramclone.enums.NotificationType;
import com.instagramclone.model.Notification;
import com.instagramclone.model.User;
import com.instagramclone.model.Follower;
import com.instagramclone.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // WebSocket for real-time updates

    // --- Convert Notification entity -> DTO safely ---
    private NotificationDTO toDTO(Notification notification) {
        UserDTO senderDto = notification.getSender() != null ? new UserDTO(notification.getSender()) : null;

        Long relatedFollowerId = null;
        String relatedFollowerStatus = null;
        if (notification.getRelatedFollower() != null) {
            relatedFollowerId = notification.getRelatedFollower().getId();
            relatedFollowerStatus = notification.getRelatedFollower().getStatus() != null
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
                relatedFollowerId,
                relatedFollowerStatus
        );
    }

    public NotificationDTO createNotification(User sender,
		            User receiver,
		            NotificationType type,
		            String content,
		            Follower relatedFollower) {
		if (sender == null || receiver == null) {
		throw new IllegalArgumentException("Sender and Receiver cannot be null");
		}
		
		// 🚨 Prevent self-notification
		if (sender.getId().equals(receiver.getId())) {
		System.out.println("⚠️ Skipping self-notification for user: " + sender.getUsername());
		return null;
		}
		
		// 🛠 Debug: log roles clearly
		System.out.println("📩 Creating notification | Sender: " + sender.getUsername() + " (id=" + sender.getId() + 
		") -> Receiver: " + receiver.getUsername() + " (id=" + receiver.getId() + ")");
		
		Notification notification = new Notification();
		notification.setSender(sender);   // ✅ always the action initiator
		notification.setReceiver(receiver); // ✅ always the one being notified
		notification.setType(type);
		notification.setContent(content);
		notification.setRead(false);
		notification.setActionCompleted(false);
		notification.setRelatedFollower(relatedFollower);
		
		notification = notificationRepository.save(notification);
		
		// ✅ Send to the actual receiver only
		messagingTemplate.convertAndSend("/topic/notifications/" + receiver.getId(), toDTO(notification));
		
		return toDTO(notification);
    }


    // --- Fetch all notifications for a user ---
    public List<NotificationDTO> getNotifications(User receiver) {
        return notificationRepository.findByReceiverOrderByCreatedAtDesc(receiver)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Fetch unread notifications ---
    public List<NotificationDTO> getUnreadNotifications(User receiver) {
        return notificationRepository.findByReceiverAndIsReadFalseOrderByCreatedAtDesc(receiver)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Mark notification as read ---
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // --- Mark action completed (e.g. Accept/Reject follow request) ---
    public void completeAction(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setActionCompleted(true);
        notificationRepository.save(notification);

        // ✅ Notify the sender (the one who triggered the notification)
        if (notification.getSender() != null) {
            messagingTemplate.convertAndSend("/topic/notifications/" + notification.getSender().getId(), toDTO(notification));
        }
    }

    // --- Utility: get notification by related Follower (for follow requests) ---
    public NotificationDTO getByFollower(Follower follower) {
        Notification notification = notificationRepository.findByRelatedFollower(follower)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        return toDTO(notification);
    }
    
 // --- Delete notification ---
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notificationRepository.delete(notification);

        // ✅ Optionally, notify the receiver UI to remove it in real-time
        if (notification.getReceiver() != null) {
            messagingTemplate.convertAndSend(
                "/topic/notifications/" + notification.getReceiver().getId(),
                "NotificationDeleted:" + notificationId
            );
        }
    }
    public void deleteByFollower(Follower follower) {
        notificationRepository.findByRelatedFollower(follower)
                .ifPresent(notificationRepository::delete);
    }
    
    public void deleteByRelatedFollower(Follower follower) {
        notificationRepository.deleteByRelatedFollower(follower);
    }



}
