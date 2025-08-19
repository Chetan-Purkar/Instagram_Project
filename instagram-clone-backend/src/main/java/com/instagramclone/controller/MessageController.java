package com.instagramclone.controller;

import com.instagramclone.dto.MessageDTO;
import com.instagramclone.dto.UserDTO;
import com.instagramclone.model.Message;
import com.instagramclone.model.User;
import com.instagramclone.repository.UserRepository;
import com.instagramclone.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    // 1. Send a message
    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO dto, Principal principal) {
        String senderUsername = principal.getName();
        
        User sender = userRepository.findByUsername(senderUsername).orElseThrow();
        Message saved = messageService.sendMessage(sender.getId(), dto.getReceiverId(), dto.getContent());
        
        return ResponseEntity.ok(new MessageDTO(saved));
    }


    // 2. Get conversation between current and selected user
    @GetMapping("/chat")
    public ResponseEntity<List<MessageDTO>> getChat(@RequestParam Long chatWithId, Principal principal) {
        String currentUsername = principal.getName();
       
        User current = userRepository.findByUsername(currentUsername).orElseThrow();
        List<Message> conversation = messageService.getConversation(current.getId(), chatWithId);

        // Convert to DTO to avoid circular serialization
        List<MessageDTO> dtos = conversation.stream().map(MessageDTO::new).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


    // 3. Get all chat users (as UserDTOs)
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllChatUsers(Principal principal) {
        String currentUsername = principal.getName();
        System.out.println("Fetching chat users for: " + currentUsername);

        User current = userRepository.findByUsername(currentUsername).orElseThrow();
        List<User> chatUsers = messageService.getAllChatUsers(current.getId());

        chatUsers.forEach(user -> System.out.println("Chat user: " + user.getUsername()));

        List<UserDTO> userDTOs = chatUsers.stream().map(UserDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // 4. Get user details by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = new UserDTO(user);
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
