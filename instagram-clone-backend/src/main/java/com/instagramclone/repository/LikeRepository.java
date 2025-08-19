// LikeRepository.java
package com.instagramclone.repository;

import com.instagramclone.model.Like;
import com.instagramclone.model.Post;
import com.instagramclone.model.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
	
    Optional<Like> findByPostAndUser(Post post, User user);
    @Transactional
    void deleteByPostAndUser(Post post, User user); 
    
    int countByPost(Post post);
}

