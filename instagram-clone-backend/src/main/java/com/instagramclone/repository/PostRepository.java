package com.instagramclone.repository;

import com.instagramclone.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Custom query to get posts by username
    @Query("SELECT p FROM Post p WHERE p.user.username = :username")
    List<Post> findByUsername(String username);
    List<Post> findByUserUsername(String username);
}
