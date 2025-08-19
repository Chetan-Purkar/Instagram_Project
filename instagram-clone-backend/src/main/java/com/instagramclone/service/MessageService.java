package com.instagramclone.service;

import com.instagramclone.model.Message;
import com.instagramclone.model.User;
import com.instagramclone.repository.MessageRepository;
import com.instagramclone.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // 1. Send a message
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    // 2. Get chat between current user and selected user
    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversationBetweenUsers(userId1, userId2);
    }

    // 3. Get all users the current user has chatted with
    public List<User> getAllChatUsers(Long currentUserId) {
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

        return userRepository.findAllById(userIds);
    }
}
