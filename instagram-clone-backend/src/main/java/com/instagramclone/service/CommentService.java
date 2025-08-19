package com.instagramclone.service;

import com.instagramclone.dto.CommentDTO;
import com.instagramclone.model.Comment;
import com.instagramclone.model.Post;
import com.instagramclone.model.User;
import com.instagramclone.repository.CommentRepository;
import com.instagramclone.repository.PostRepository;
import com.instagramclone.repository.UserRepository;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    
 // Fetch comments for a specific post
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream()
                .map(comment -> {
                    String username = comment.getUser().getUsername();

                    // Get the profile image in Base64 format
                    byte[] profileImageBytes = comment.getUser().getProfileImage();
                    String profileImage = (profileImageBytes != null)
                            ? Base64.getEncoder().encodeToString(profileImageBytes)
                            : null;

                    return new CommentDTO(
                            comment.getId(),
                            comment.getText(),
                            username,
                            comment.getPost().getId(),
                            profileImage // 🔥 Include profile image here
                    );
                })
                .collect(Collectors.toList());
    }

    public Comment addComment(Long postId, String username, String text) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("comments User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setUser(user);
        comment.setPost(post);
        
        return commentRepository.save(comment);
    }
    
    
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    
    
    public ResponseEntity<String> deleteComment(Long commentId, String currentUsername) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
        }

        Comment comment = optionalComment.get();

        if (!comment.getUser().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: You can only delete your own comments.");
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}
