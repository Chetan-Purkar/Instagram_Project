package com.instagramclone.service;

import com.instagramclone.dto.CommentDTO;
import com.instagramclone.model.Comment;
import com.instagramclone.model.Post;
import com.instagramclone.model.User;
import com.instagramclone.repository.CommentRepository;
import com.instagramclone.repository.PostRepository;
import com.instagramclone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // ✅ Fetch comments for a post
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ Add a new comment
    public Comment addComment(Long postId, String username, String text) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setUser(user);
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    // ✅ Get comment by ID
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    // ✅ Delete comment with user verification
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
