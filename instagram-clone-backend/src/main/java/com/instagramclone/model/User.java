package com.instagramclone.model;

import com.instagramclone.enums.AccountPrivacy;
import com.instagramclone.enums.MessagePrivacy;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    /* ------------------- 📌 Primary Key ------------------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ------------------- 📌 Basic Info ------------------- */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(length = 250)
    private String bio;

    @Column(nullable = false)
    private String password;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    /* ------------------- 📌 Relationships ------------------- */

    // Posts created by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    // Followers → Users who follow this user
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Follower> followers = new ArrayList<>();

    // Following → Users this user follows
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Follower> following = new ArrayList<>();

    // Stories created by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Story> stories = new ArrayList<>();

    // Close Friends list
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_close_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> closeFriends = new HashSet<>();

    // Blocked users
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_blocks",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "blocked_user_id")
    )
    private Set<User> blockedUsers = new HashSet<>();

    /* ------------------- 📌 Privacy & Security ------------------- */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountPrivacy accountPrivacy = AccountPrivacy.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessagePrivacy messagePrivacy = MessagePrivacy.ANYONE;

    @Column(nullable = false)
    private boolean tagApprovalRequired = false;

    /* ------------------- 📌 Constructors ------------------- */
    public User() {
    }

    public User(String username, String name, String email, String bio, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.password = password;
    }

    /* ------------------- 📌 Getters & Setters ------------------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    public List<Follower> getFollowers() { return followers; }
    public void setFollowers(List<Follower> followers) { this.followers = followers; }

    public List<Follower> getFollowing() { return following; }
    public void setFollowing(List<Follower> following) { this.following = following; }

    public List<Story> getStories() { return stories; }
    public void setStories(List<Story> stories) { this.stories = stories; }

    public Set<User> getCloseFriends() { return closeFriends; }
    public void setCloseFriends(Set<User> closeFriends) { this.closeFriends = closeFriends; }

    public Set<User> getBlockedUsers() { return blockedUsers; }
    public void setBlockedUsers(Set<User> blockedUsers) { this.blockedUsers = blockedUsers; }

    public AccountPrivacy getAccountPrivacy() { return accountPrivacy; }
    public void setAccountPrivacy(AccountPrivacy accountPrivacy) { this.accountPrivacy = accountPrivacy; }

    public MessagePrivacy getMessagePrivacy() { return messagePrivacy; }
    public void setMessagePrivacy(MessagePrivacy messagePrivacy) { this.messagePrivacy = messagePrivacy; }

    public boolean isTagApprovalRequired() { return tagApprovalRequired; }
    public void setTagApprovalRequired(boolean tagApprovalRequired) { this.tagApprovalRequired = tagApprovalRequired; }

    /* ------------------- 📌 Helper Methods ------------------- */
    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setUser(null);
    }
}
