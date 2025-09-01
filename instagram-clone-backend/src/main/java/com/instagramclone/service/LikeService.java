package com.instagramclone.service;

import com.instagramclone.model.Like;
import com.instagramclone.model.Post;
import com.instagramclone.model.User;
import com.instagramclone.repository.LikeRepository;
import com.instagramclone.repository.PostRepository;
import com.instagramclone.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Toggle like status for a post by a user
     * @return true if post is now liked, false if unliked
     */
    public boolean toggleLike(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false; // Unliked
        }

        likeRepository.save(new Like(post, user));
        return true; // Liked
    }
}
