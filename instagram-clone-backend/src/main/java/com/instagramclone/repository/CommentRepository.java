package com.instagramclone.repository;

import com.instagramclone.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing Comment entities.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

   
    List<Comment> findByPostId(Long postId);

  
    List<Comment> findByUserId(Long userId);

    
    long countByPostId(Long postId);
}
