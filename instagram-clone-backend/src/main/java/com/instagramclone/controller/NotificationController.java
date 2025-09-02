package com.instagramclone.controller;

import com.instagramclone.dto.NotificationDTO;
import com.instagramclone.model.Follower;
import com.instagramclone.model.User;
import com.instagramclone.repository.UserRepository;
import com.instagramclone.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService,
                                  UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // 🔹 Helper to get current user
    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationDTO>> getNotifications(Principal principal) {
        User currentUser = getCurrentUser(principal); // assuming this returns a User entity
        System.out.println("Fetching notifications for user: " + currentUser.getUsername());
        List<NotificationDTO> notifications = notificationService.getNotifications(currentUser);
        System.out.println("Notifications: " + notifications);
        return ResponseEntity.ok(notifications);
    }



    // --- Get unread notifications ---
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(Principal principal) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(getCurrentUser(principal)));
    }

    // --- Mark notification as read ---
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("✅ Notification marked as read");
    }

    // --- Mark notification action completed (accept/reject follow request) ---
    @PutMapping("/{id}/complete")
    public ResponseEntity<String> completeAction(@PathVariable Long id) {
        notificationService.completeAction(id);
        return ResponseEntity.ok("✅ Notification action marked as completed");
    }

    // --- Delete a notification by ID ---
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("🗑️ Notification deleted successfully");
    }

    // --- Delete notification by related follower ---
    @DeleteMapping("/follower/{followerId}")
    public ResponseEntity<String> deleteByFollower(@PathVariable Long followerId) {
        Follower follower = new Follower();
        follower.setId(followerId);
        notificationService.deleteByRelatedFollower(follower);
        return ResponseEntity.ok("🗑️ Notification related to follower deleted successfully");
    }
}
