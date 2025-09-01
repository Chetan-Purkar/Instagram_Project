package com.instagramclone.service;

import com.instagramclone.dto.StoryLikeDTO;
import com.instagramclone.model.Story;
import com.instagramclone.model.StoryLike;
import com.instagramclone.model.User;
import com.instagramclone.repository.StoryLikeRepository;
import com.instagramclone.repository.StoryRepository;
import com.instagramclone.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoryLikeService {

    private final StoryLikeRepository storyLikeRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    public StoryLikeService(StoryLikeRepository storyLikeRepository, StoryRepository storyRepository, UserRepository userRepository) {
        this.storyLikeRepository = storyLikeRepository;
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
    }

    // ✅ Like a story
    public StoryLikeDTO likeStory(Long storyId, Long userId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<StoryLike> existingLike = storyLikeRepository.findByStoryAndUser(story, user);
        if (existingLike.isPresent()) {
            return convertToDTO(existingLike.get()); // Already liked
        }

        StoryLike storyLike = new StoryLike();
        storyLike.setStory(story);
        storyLike.setUser(user);
        storyLike.setLikedAt(LocalDateTime.now());

        StoryLike savedLike = storyLikeRepository.save(storyLike);
        return convertToDTO(savedLike);
    }

    // Unlike
    public void unlikeStory(Long storyId, Long userId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        storyLikeRepository.findByStoryAndUser(story, user)
                .ifPresent(storyLikeRepository::delete);
    }
    
 // ✅ Get all likes for a story
    public List<StoryLikeDTO> getLikesForStory(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        return storyLikeRepository.findByStory(story)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    // ✅ Count likes
    public long countLikes(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));
        return storyLikeRepository.countByStory(story);
    }

    // ✅ Mapper: Entity → DTO
    private StoryLikeDTO convertToDTO(StoryLike storyLike) {
        return new StoryLikeDTO(
                storyLike.getId(),
                storyLike.getStory().getId(),
                storyLike.getUser().getId(),
                storyLike.getUser().getUsername(),
                storyLike.getUser().getProfileImage(),
                storyLike.getLikedAt()
        );
    }
}
