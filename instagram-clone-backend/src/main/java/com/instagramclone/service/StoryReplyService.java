package com.instagramclone.service;

import com.instagramclone.dto.StoryReplyDTO;
import com.instagramclone.model.Message;
import com.instagramclone.model.Story;
import com.instagramclone.model.StoryReply;
import com.instagramclone.model.User;
import com.instagramclone.repository.MessageRepository;
import com.instagramclone.repository.StoryReplyRepository;
import com.instagramclone.repository.StoryRepository;
import com.instagramclone.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class StoryReplyService {

    @Autowired
    private StoryReplyRepository storyReplyRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MessageRepository messageRepository; 
    

    /**
     * Add a reply to a story
     */
    public StoryReplyDTO addReply(Long storyId, String replyMessage, String username) {
        // Get authenticated user
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Get story
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        // Create story reply
        StoryReply reply = new StoryReply();
        reply.setStory(story);
        reply.setUser(user);
        reply.setReplyMessage(replyMessage);
        reply.setRepliedAt(LocalDateTime.now());

        StoryReply savedReply = storyReplyRepository.save(reply);

        // ✅ ALSO save this as a Message (goes to story owner)
        Message message = new Message();
        message.setSender(user); // replier
        message.setReceiver(story.getUser()); // story owner
        message.setContent(replyMessage);
        message.setTimestamp(LocalDateTime.now());
        message.setStory(story); // link story (must be added in Message entity)
        
        messageRepository.save(message);

        return mapToDTO(savedReply);
    }

    /**
     * Get all replies for a story
     */
    public List<StoryReplyDTO> getRepliesByStory(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        return storyReplyRepository.findByStoryOrderByRepliedAtDesc(story)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all replies made by a user
     */
    public List<StoryReplyDTO> getRepliesByUser(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return storyReplyRepository.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Count replies for a story
     */
    public long countRepliesByStory(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));
        return storyReplyRepository.countByStory(story);
    }

    /**
     * Convert entity -> DTO
     */
    private StoryReplyDTO mapToDTO(StoryReply reply) {
        User u = reply.getUser();
        String base64Image = toBase64(u.getProfileImage()); // byte[] -> String
        return new StoryReplyDTO(
                reply.getId(),
                reply.getStory().getId(),
                u.getId(),
                u.getUsername(),
                base64Image,
                reply.getReplyMessage(),               // <-- FIX: use entity's 'message'
                reply.getRepliedAt()
        );
    }
    
    
    private String toBase64(byte[] data) {
        return (data == null || data.length == 0) ? null : Base64.getEncoder().encodeToString(data);
    }

    public List<StoryReplyDTO> getRepliesToUserStories(Long userId) {
        // Fetch stories of the user
        List<Story> userStories = storyRepository.findByUserOrderByCreatedAtDesc(userRepository.findById(userId).get());
        
        List<StoryReplyDTO> allReplies = new ArrayList<>();
        for (Story story : userStories) {
            List<StoryReplyDTO> replies = getRepliesByStory(story.getId());
            allReplies.addAll(replies);
        }
        return allReplies;
    }

}
