package com.instagramclone.service;

import com.instagramclone.dto.FollowerDto;
import com.instagramclone.model.Follower;
import com.instagramclone.model.User;
import com.instagramclone.repository.FollowerRepository;
import com.instagramclone.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
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

    public String toggleFollow(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower user not found"));
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("Following user not found"));

        Optional<Follower> existingFollow = followerRepository.findByFollowerAndFollowing(follower, following);
        if (existingFollow.isPresent()) {
            followerRepository.delete(existingFollow.get());
            return "Unfollowed";
        } else {
            Follower newFollow = new Follower();
            newFollow.setFollower(follower);
            newFollow.setFollowing(following);
            followerRepository.save(newFollow);
            return "Followed";
        }
    }
    
    public boolean isFollowing(String currentUsername, String targetUsername) {
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow();
        User targetUser = userRepository.findByUsername(targetUsername).orElseThrow();
        return followerRepository.findByFollowerAndFollowing(currentUser, targetUser).isPresent();
    }


    public List<FollowerDto> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followerRepository.findByFollowing(user).stream()
                .map(follow -> {
                    User followerUser = follow.getFollower();
                    String profileImage = followerUser.getProfileImage() != null
                            ? Base64.getEncoder().encodeToString(followerUser.getProfileImage())
                            : null;

                    return new FollowerDto(
                            followerUser.getUsername(),     // followerUsername
                            username,                       // followingUsername (this user's username)
                            profileImage,                   // profile image of the follower
                            username                        // profile being viewed
                    );
                })
                .collect(Collectors.toList());
    }

    public List<FollowerDto> getFollowing(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followerRepository.findByFollower(user).stream()
                .map(follow -> {
                    User followingUser = follow.getFollowing();
                    String profileImage = followingUser.getProfileImage() != null
                            ? Base64.getEncoder().encodeToString(followingUser.getProfileImage())
                            : null;

                    return new FollowerDto(
                            username,                        // followerUsername (this user)
                            followingUser.getUsername(),     // followingUsername
                            profileImage,                    // profile image of the followed user
                            username                         // profile being viewed
                    );
                })
                .collect(Collectors.toList());
    }

    public List<FollowerDto> searchFollowersByUsername(String username) {
        return followerRepository.searchFollowersByUsername(username).stream()
                .map(follow -> {
                    User followerUser = follow.getFollower();
                    String profileImage = followerUser.getProfileImage() != null
                            ? Base64.getEncoder().encodeToString(followerUser.getProfileImage())
                            : null;

                    return new FollowerDto(
                            followerUser.getUsername(),
                            follow.getFollowing().getUsername(),
                            profileImage,
                            follow.getFollowing().getUsername()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<FollowerDto> searchFollowingByUsername(String username) {
        return followerRepository.searchFollowingByUsername(username).stream()
                .map(follow -> {
                    User followingUser = follow.getFollowing();
                    String profileImage = followingUser.getProfileImage() != null
                            ? Base64.getEncoder().encodeToString(followingUser.getProfileImage())
                            : null;

                    return new FollowerDto(
                            follow.getFollower().getUsername(),
                            followingUser.getUsername(),
                            profileImage,
                            follow.getFollower().getUsername()
                    );
                })
                .collect(Collectors.toList());
    }

}
