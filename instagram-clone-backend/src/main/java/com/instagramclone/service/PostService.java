package com.instagramclone.service;

import com.instagramclone.model.Post;
import com.instagramclone.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // Get all posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    // Get posts by username
    public List<Post> getPostByUsername(String username) {
        return postRepository.findByUserUsername(username);
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
