package com.instagramclone.dto;

public class FollowerDto {

    private String username;
    private String followerUsername;
    private String followingUsername;
    private String profileImage;

    // Default constructor
    public FollowerDto() {}

    // Constructor with parameters
    public FollowerDto(String followerUsername, String followingUsername, String profileImage, String username) {
        this.username = username;
        this.followerUsername = followerUsername;
        this.followingUsername = followingUsername;
        this.profileImage = profileImage;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFollowerUsername() {
        return followerUsername;
    }

    public void setFollowerUsername(String followerUsername) {
        this.followerUsername = followerUsername;
    }

    public String getFollowingUsername() {
        return followingUsername;
    }

    public void setFollowingUsername(String followingUsername) {
        this.followingUsername = followingUsername;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
