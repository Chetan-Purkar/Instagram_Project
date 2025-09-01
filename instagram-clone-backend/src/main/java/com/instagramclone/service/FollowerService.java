package com.instagramclone.service;

import com.instagramclone.dto.FollowerDto;
import com.instagramclone.enums.AccountPrivacy;
import com.instagramclone.enums.FollowStatus;
import com.instagramclone.model.Follower;
import com.instagramclone.model.User;
import com.instagramclone.repository.FollowerRepository;
import com.instagramclone.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    public FollowerService(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    // ✅ Follow / Unfollow toggle
    public String toggleFollow(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower user not found"));
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("Following user not found"));

        Optional<Follower> existingFollow = followerRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            followerRepository.delete(existingFollow.get());
            return "Unfollowed";
        }

        Follower newFollow = new Follower();
        newFollow.setFollower(follower);
        newFollow.setFollowing(following);

        if (following.getAccountPrivacy() == AccountPrivacy.PRIVATE) {
            newFollow.setStatus(FollowStatus.PENDING); // Follow request
            followerRepository.save(newFollow);
            return "Follow request sent";
        }

        newFollow.setStatus(FollowStatus.ACCEPTED); // Auto-accepted
        followerRepository.save(newFollow);
        return "Followed";
    }

    // ✅ Check if user A follows user B
    public boolean isFollowing(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername).orElseThrow();
        User following = userRepository.findByUsername(followingUsername).orElseThrow();
        return followerRepository.findByFollowerAndFollowing(follower, following).isPresent();
    }
    
    public String getFollowStatus(String currentUsername, String targetUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        Optional<Follower> relation = followerRepository.findByFollowerAndFollowing(currentUser, targetUser);

        if (relation.isEmpty()) {
            return "FOLLOW";
        }

        switch (relation.get().getStatus()) {
            case ACCEPTED:
                return "FOLLOWING";
            case PENDING:
                return "PENDING";
            default:
                return "FOLLOW";
        }
    }

    // ✅ Get list of followers
    public List<FollowerDto> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followerRepository.findByFollowing(user)
                .stream()
                .map(f -> FollowerDto.fromEntity(f, user.getUsername(), true))
                .collect(Collectors.toList());
    }

    // ✅ Get list of following
    public List<FollowerDto> getFollowing(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followerRepository.findByFollower(user)
                .stream()
                .map(f -> FollowerDto.fromEntity(f, user.getUsername(), false))
                .collect(Collectors.toList());
    }
    
   


    // ✅ Accept follow request
    public String acceptFollowRequest(Long requestId) {
        Follower follow = followerRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Follow request not found"));

        if (follow.getStatus() == FollowStatus.PENDING) {
            follow.setStatus(FollowStatus.ACCEPTED);
            followerRepository.save(follow);
            return "Follow request accepted";
        }
        return "Invalid request";
    }

    // ✅ Reject follow request
    public String rejectFollowRequest(Long requestId) {
        Follower follow = followerRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Follow request not found"));

        if (follow.getStatus() == FollowStatus.PENDING) {
            followerRepository.delete(follow);
            return "Follow request rejected";
        }
        return "Invalid request";
    }

    // ✅ Get pending follow requests
    public List<FollowerDto> getPendingRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followerRepository.findByFollowing(user)
                .stream()
                .filter(f -> f.getStatus() == FollowStatus.PENDING)
                .map(f -> FollowerDto.fromEntity(f, user.getUsername(), true))
                .collect(Collectors.toList());
    }

    // ✅ Search followers
    public List<FollowerDto> searchFollowersByUsername(String username) {
        return followerRepository.searchFollowersByUsername(username)
                .stream()
                .map(f -> FollowerDto.fromEntity(f, f.getFollowing().getUsername(), true))
                .collect(Collectors.toList());
    }

    // ✅ Search following
    public List<FollowerDto> searchFollowingByUsername(String username) {
        return followerRepository.searchFollowingByUsername(username)
                .stream()
                .map(f -> FollowerDto.fromEntity(f, f.getFollower().getUsername(), false))
                .collect(Collectors.toList());
    }

	
}
