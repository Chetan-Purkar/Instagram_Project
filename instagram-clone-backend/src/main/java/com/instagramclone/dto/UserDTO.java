package com.instagramclone.dto;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import com.instagramclone.model.User;

public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String bio;
    private String profileImage;
    private List<PostDTO> posts;
    private boolean likedByCurrentUser;

    // Existing constructor (without likedByCurrentUser)
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.profileImage = (user.getProfileImage() != null)
                ? Base64.getEncoder().encodeToString(user.getProfileImage())
                : null;
        this.posts = (user.getPosts() != null)
                ? user.getPosts().stream().map(PostDTO::new).collect(Collectors.toList())
                : null;
    }

    // New constructor (with likedByCurrentUser)
    public UserDTO(User user, boolean likedByCurrentUser) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.profileImage = (user.getProfileImage() != null)
                ? Base64.getEncoder().encodeToString(user.getProfileImage())
                : null;
        this.posts = (user.getPosts() != null)
                ? user.getPosts().stream().map(PostDTO::new).collect(Collectors.toList())
                : null;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    // Getters and Setters
    public UserDTO(Long id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

}
