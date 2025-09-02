package com.instagramclone.repository;

import com.instagramclone.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Paginated query to get posts by username
    @Query("SELECT p FROM Post p WHERE p.user.username = :username ORDER BY p.createdAt DESC")
    Page<Post> findByUsername(String username, Pageable pageable);
    
    // Alternative without custom query
    Page<Post> findByUserUsernameOrderByCreatedAtDesc(String username, Pageable pageable);
}
