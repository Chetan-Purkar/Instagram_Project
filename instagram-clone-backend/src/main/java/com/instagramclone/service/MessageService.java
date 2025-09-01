package com.instagramclone.service;

import com.instagramclone.dto.MessageDTO;
import com.instagramclone.dto.StoryReplyDTO;
import com.instagramclone.enums.MessagePrivacy;
import com.instagramclone.enums.MessageStatus;
import com.instagramclone.model.Message;
import com.instagramclone.model.User;
import com.instagramclone.repository.FollowerRepository;
import com.instagramclone.repository.MessageRepository;
import com.instagramclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followRepository;

    @Autowired
    private StoryReplyService storyReplyService;

    @Autowired
    private UserService userService;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          FollowerRepository followRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    /* ------------------- 📌 1. Send a message ------------------- */
    public MessageDTO sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        // ❌ If either has blocked the other → deny
        if (sender.getBlockedUsers().contains(receiver) || receiver.getBlockedUsers().contains(sender)) {
            throw new AccessDeniedException("Messaging not allowed (user blocked).");
        }

        // 🔒 Enforce receiver’s privacy settings
        if (receiver.getMessagePrivacy() == MessagePrivacy.NO_ONE) {
            throw new AccessDeniedException("This user is not accepting messages.");
        }

        if (receiver.getMessagePrivacy() == MessagePrivacy.FOLLOWERS) {
            boolean isFollowing = followRepository.findByFollowerAndFollowing(sender, receiver).isPresent();
            if (!isFollowing) {
                throw new RuntimeException("This user only accepts messages from followers.");
            }
        }

        // ✅ Save message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);

        return new MessageDTO(messageRepository.save(message));
    }

    
    /* ------------------- 📌 2. Convert story replies into messages ------------------- */
    public List<MessageDTO> getStoryRepliesAsMessages(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<StoryReplyDTO> replies = storyReplyService.getRepliesToUserStories(user.getId());

        return replies.stream().map(reply -> {
            MessageDTO dto = new MessageDTO();
            dto.setSenderId(reply.getUserId());
            dto.setReceiverId(user.getId());
            dto.setContent(reply.getReplayMessage());
            dto.setTimestamp(reply.getRepliedAt());
            dto.setStoryId(reply.getStoryId());
            dto.setStoryPreview(reply.getProfileImage()); 
            dto.setStatus(MessageStatus.SENT);
            return dto;
        }).collect(Collectors.toList());
    }

    /* ------------------- 📌 3. Get conversation between two users ------------------- */
    public List<MessageDTO> getConversation(Long userId1, Long userId2) {
        User u1 = userRepository.findById(userId1).orElseThrow();
        User u2 = userRepository.findById(userId2).orElseThrow();

        // ❌ Don’t show if blocked
        if (u1.getBlockedUsers().contains(u2) || u2.getBlockedUsers().contains(u1)) {
            return Collections.emptyList();
        }

        return messageRepository.findConversationBetweenUsers(userId1, userId2)
                .stream()
                .filter(msg -> !(msg.isDeletedBySender() && msg.getSender().getId().equals(userId1)))
                .filter(msg -> !(msg.isDeletedByReceiver() && msg.getReceiver().getId().equals(userId1)))
                .map(MessageDTO::new)
                .collect(Collectors.toList());
    }

    /* ------------------- 📌 4. Get all chat users ------------------- */
    public List<User> getAllChatUsers(Long currentUserId) {
        User me = userRepository.findById(currentUserId).orElseThrow();
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(currentUserId, currentUserId);

        Set<Long> userIds = new HashSet<>();
        for (Message msg : messages) {
            if (!msg.getSender().getId().equals(currentUserId)) {
                userIds.add(msg.getSender().getId());
            }
            if (!msg.getReceiver().getId().equals(currentUserId)) {
                userIds.add(msg.getReceiver().getId());
            }
        }

        List<User> users = userRepository.findAllById(userIds);

        // ❌ filter out blocked users
        return users.stream()
                .filter(u -> !me.getBlockedUsers().contains(u) && !u.getBlockedUsers().contains(me))
                .collect(Collectors.toList());
    }

    /* ------------------- 📌 5. Update message status ------------------- */
    public MessageDTO updateMessageStatus(Long messageId, MessageStatus status) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        message.setStatus(status);

        if (status == MessageStatus.SEEN) {
            message.setSeenAt(LocalDateTime.now());
        }

        return new MessageDTO(messageRepository.save(message));
    }

    
    /* ------------------- 📌 6. Soft delete message ------------------- */
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId).orElseThrow();

        if (message.getSender().getId().equals(userId)) {
            message.setDeletedBySender(true);
        } else if (message.getReceiver().getId().equals(userId)) {
            message.setDeletedByReceiver(true);
        } else {
            throw new AccessDeniedException("You cannot delete this message.");
        }

        messageRepository.save(message);
    }

	
}
