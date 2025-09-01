package com.instagramclone.dto;

public class CommentDTO {
    private Long id;
    private String text;
    private String username;
    private Long postId;
    private String profileImage; // base64 string for frontend

    // Default constructor for frameworks like Jackson
    public CommentDTO() {}

    // All-args constructor
    public CommentDTO(Long id, String text, String username, Long postId, String profileImage) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.postId = postId;
        this.profileImage = profileImage;
    }

    // ✅ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    // ✅ Optional: Factory method to convert from Comment entity
    public static CommentDTO fromEntity(com.instagramclone.model.Comment comment) {
        return new CommentDTO(
            comment.getId(),
            comment.getText(),
            comment.getUser().getUsername(),
            comment.getPost() != null ? comment.getPost().getId() : null,
            comment.getUser().getProfileImage() != null
                ? java.util.Base64.getEncoder().encodeToString(comment.getUser().getProfileImage())
                : null
        );
    }
}
