package com.instagramclone.repository;

import com.instagramclone.model.Notification;
import com.instagramclone.model.User;
import com.instagramclone.model.Follower;
import com.instagramclone.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // --- Get all notifications for a user ordered by creation date (most recent first) ---
    @Query("SELECT n FROM Notification n " +
           "LEFT JOIN FETCH n.sender " +
           "LEFT JOIN FETCH n.relatedFollower " +
           "WHERE n.receiver = :receiver " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findByReceiverOrderByCreatedAtDesc(@Param("receiver") User receiver);

    // --- Get unread notifications ---
    @Query("SELECT n FROM Notification n " +
           "LEFT JOIN FETCH n.sender " +
           "LEFT JOIN FETCH n.relatedFollower " +
           "WHERE n.receiver = :receiver AND n.isRead = false " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findByReceiverAndIsReadFalseOrderByCreatedAtDesc(@Param("receiver") User receiver);

    // --- Get notification by sender, receiver, and type (use Enum, not String) ---
    Optional<Notification> findBySenderAndReceiverAndType(User sender, User receiver, NotificationType type);

    // --- Get notification by related Follower entity ---
    Optional<Notification> findByRelatedFollower(Follower follower);

    // --- Delete notification(s) by related Follower ---
    void deleteByRelatedFollower(Follower follower);
}
