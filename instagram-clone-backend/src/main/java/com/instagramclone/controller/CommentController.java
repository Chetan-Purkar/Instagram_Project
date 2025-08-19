package com.instagramclone.controller;

import com.instagramclone.dto.CommentDTO;
import com.instagramclone.model.Comment;
import com.instagramclone.service.CommentService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    
 // Fetch all comments for a specific post
    @GetMapping("/posts/{postId}/comments")
    public List<CommentDTO> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    // Add Comment
    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody CommentDTO commentDTO) {
        Comment comment = commentService.addComment(commentDTO.getPostId(), commentDTO.getUsername(), commentDTO.getText());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, @RequestParam String username) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Ensure user is deleting their own comment
        if (!currentUsername.equals(username)) {
            return ResponseEntity.status(401)
                    .body("Unauthorized: You can only delete your own comments.");
        }

        return commentService.deleteComment(commentId, currentUsername);
    }

}
