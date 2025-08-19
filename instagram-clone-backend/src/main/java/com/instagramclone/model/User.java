package com.instagramclone.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String name;
    private String email;
    private String bio;
    private String password;

    

	@Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profileImage; // Changed to byte[] and added @Lob/@Column

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;
    
    
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follower> followers; // Users who follow this user

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follower> following; // Users this user follows
    

    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }

    public List<Follower> getFollowing() {
        return following;
    }

    public void setFollowing(List<Follower> following) {
        this.following = following;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getProfileImage() { // Changed getter to return byte[]
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) { // Changed setter to accept byte[]
        this.profileImage = profileImage;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}