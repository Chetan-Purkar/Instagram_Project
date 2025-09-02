package com.instagramclone.repository;

import com.instagramclone.model.Story;
import com.instagramclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {

    // Fetch all stories
    List<Story> findAll();

    // Fetch stories by a specific user
    List<Story> findByUserOrderByCreatedAtDesc(User user);

    // Fetch only active stories of a specific user (not expired)
    List<Story> findByUserAndExpiresAtAfterOrderByCreatedAtDesc(User user, LocalDateTime now);
    
    List<Story> findByUserInAndExpiresAtAfter(List<User> users, LocalDateTime now);

    // Fetch all active stories
    List<Story> findByExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime now);

    // Fetch stories of users followed by current user (not expired)
    @Query("SELECT s FROM Story s WHERE s.user IN " +
           "(SELECT f.following FROM Follower f WHERE f.follower = :currentUser) " +
           "AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    List<Story> findStoriesOfFollowedUsers(
            @Param("currentUser") Optional<User> optional,
            @Param("now") LocalDateTime now
    );

    // Fetch stories of users that the given user is following
    @Query("SELECT s FROM Story s " +
           "WHERE s.user IN (SELECT f.following FROM Follower f WHERE f.follower.id = :userId) " +
           "AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    List<Story> findStoriesOfFollowingUsers(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Delete expired stories
    void deleteByExpiresAtBefore(LocalDateTime now);
}
