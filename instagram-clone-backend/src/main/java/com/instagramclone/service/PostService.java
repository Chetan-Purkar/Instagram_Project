package com.instagramclone.service;

import com.instagramclone.model.Post;
import com.instagramclone.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Create a new post
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // Get paginated posts
    public Page<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAll(pageable);
    }
    
    // Get paginated posts by username
    public Page<Post> getPostsByUsername(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByUserUsernameOrderByCreatedAtDesc(username, pageable);
    }

    // Delete a post by ID
    public boolean deletePost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }
}
