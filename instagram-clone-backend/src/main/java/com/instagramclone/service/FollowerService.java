package com.instagramclone.service;

import com.instagramclone.dto.FollowerDto;
import com.instagramclone.enums.AccountPrivacy;
import com.instagramclone.enums.FollowStatus;
import com.instagramclone.enums.NotificationType;
import com.instagramclone.model.Follower;
import com.instagramclone.model.User;
import com.instagramclone.repository.FollowerRepository;
import com.instagramclone.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FollowerService(FollowerRepository followerRepository,
                           UserRepository userRepository,
                           NotificationService notificationService) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

 // --- Toggle Follow / Unfollow ---
    @Transactional
    public String toggleFollow(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower user not found"));
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("Following user not found"));

        Optional<Follower> existingFollow = followerRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            followerRepository.delete(existingFollow.get()); // now works inside transaction
            return "Unfollowed";
        }

        Follower newFollow = new Follower();
        newFollow.setFollower(follower);
        newFollow.setFollowing(following);

        if (following.getAccountPrivacy() == AccountPrivacy.PRIVATE) {
            newFollow.setStatus(FollowStatus.PENDING);
            followerRepository.save(newFollow);
            notificationService.createNotification(
                    follower, following,
                    NotificationType.FOLLOW_REQUEST,
                    follower.getUsername() + " wants to follow you",
                    newFollow
            );
            return "Follow request sent";
        }

        newFollow.setStatus(FollowStatus.ACCEPTED);
        followerRepository.save(newFollow);

        notificationService.createNotification(
                follower, following,
                NotificationType.FOLLOW,
                follower.getUsername() + " started following you",
                newFollow
        );

        return "Followed";
    }


    // --- Check if user A follows user B ---
    public boolean isFollowing(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower user not found"));
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("Following user not found"));
        return followerRepository.findByFollowerAndFollowing(follower, following).isPresent();
    }

    // --- Get follow status (FOLLOWING, PENDING, FOLLOW) ---
    public String getFollowStatus(String currentUsername, String targetUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        return followerRepository.findByFollowerAndFollowing(currentUser, targetUser)
                .map(f -> {
                    switch (f.getStatus()) {
                        case ACCEPTED: return "FOLLOWING";
                        case PENDING: return "PENDING";
                        default: return "FOLLOW";
                    }
                })
                .orElse("FOLLOW");
    }

    // --- Accept a follow request ---
    public String acceptFollowRequest(Long requestId) {
        Follower request = getFollowerById(requestId);

        if (request.getStatus() != FollowStatus.PENDING) {
            throw new RuntimeException("Invalid request");
        }

        request.setStatus(FollowStatus.ACCEPTED);
        followerRepository.save(request);

        // Delete the original request notification
        notificationService.deleteByFollower(request);

        // Notify the requester that their request was accepted
        notificationService.createNotification(
                request.getFollowing(), // accepted user
                request.getFollower(),  // requester
                NotificationType.FOLLOW_ACCEPTED,
                request.getFollowing().getUsername() + " accepted your follow request",
                request
        );

        return "Follow request accepted";
    }


    // --- Reject a follow request ---
    public String rejectFollowRequest(Long requestId) {
        Follower request = getFollowerById(requestId);

        if (request.getStatus() != FollowStatus.PENDING) {
            throw new RuntimeException("Invalid request");
        }

        // Delete the follow request notification first
        notificationService.deleteByFollower(request);

        followerRepository.delete(request);

        // (Optional) Notify requester that their request was rejected
        notificationService.createNotification(
                request.getFollowing(),
                request.getFollower(),
                NotificationType.FOLLOW_REJECTED,
                request.getFollowing().getUsername() + " rejected your follow request",
                null // 🚨 Don't pass deleted follower here
        );

        return "Follow request rejected";
    }


    // --- Get pending follow requests ---
    public List<FollowerDto> getPendingRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followerRepository.findByFollowing(user)
                .stream()
                .filter(f -> f.getStatus() == FollowStatus.PENDING)
                .map(f -> FollowerDto.fromEntity(f, user.getUsername(), true))
                .collect(Collectors.toList());
    }

    // --- Get followers ---
    public List<FollowerDto> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followerRepository.findByFollowing(user)
                .stream()
                .map(f -> FollowerDto.fromEntity(f, user.getUsername(), true))
                .collect(Collectors.toList());
    }

    // --- Get following ---
    public List<FollowerDto> getFollowing(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followerRepository.findByFollower(user)
                .stream()
                .map(f -> FollowerDto.fromEntity(f, user.getUsername(), false))
                .collect(Collectors.toList());
    }

    // --- Search followers ---
    public List<FollowerDto> searchFollowersByUsername(String username) {
        return followerRepository.searchFollowersByUsername(username)
                .stream()
                .map(f -> FollowerDto.fromEntity(f, f.getFollowing().getUsername(), true))
                .collect(Collectors.toList());
    }

    // --- Search following ---
    public List<FollowerDto> searchFollowingByUsername(String username) {
        return followerRepository.searchFollowingByUsername(username)
                .stream()
                .map(f -> FollowerDto.fromEntity(f, f.getFollower().getUsername(), false))
                .collect(Collectors.toList());
    }

    // --- Helper ---
    public Follower getFollowerById(Long id) {
        return followerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Follow request not found"));
    }
}
