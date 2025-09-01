package com.instagramclone.dto;

import com.instagramclone.model.Follower;
import java.util.Base64;

public class FollowerDto {

    private String username;
    private String followerUsername;
    private String followingUsername;
    private String profileImage;

    public FollowerDto() {}

    public FollowerDto(String followerUsername, String followingUsername, String profileImage, String username) {
        this.username = username;
        this.followerUsername = followerUsername;
        this.followingUsername = followingUsername;
        this.profileImage = profileImage;
    }

    // Factory method to create DTO from entity
    public static FollowerDto fromEntity(Follower f, String profileBeingViewed, boolean isFollower) {
        String profileImage = null;
        if (isFollower && f.getFollower().getProfileImage() != null) {
            profileImage = Base64.getEncoder().encodeToString(f.getFollower().getProfileImage());
        } else if (!isFollower && f.getFollowing().getProfileImage() != null) {
            profileImage = Base64.getEncoder().encodeToString(f.getFollowing().getProfileImage());
        }

        String followerUsername = f.getFollower().getUsername();
        String followingUsername = f.getFollowing().getUsername();

        return new FollowerDto(followerUsername, followingUsername, profileImage, profileBeingViewed);
    }

    // Getters & setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFollowerUsername() { return followerUsername; }
    public void setFollowerUsername(String followerUsername) { this.followerUsername = followerUsername; }
    public String getFollowingUsername() { return followingUsername; }
    public void setFollowingUsername(String followingUsername) { this.followingUsername = followingUsername; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
