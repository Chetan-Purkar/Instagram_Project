package com.instagramclone.controller;

import com.instagramclone.dto.MessageDTO;
import com.instagramclone.dto.UserDTO;
import com.instagramclone.enums.MessageStatus;
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

    /* ------------------- 📌 1. Send a message ------------------- */
    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO dto, Principal principal) {
        String senderUsername = principal.getName();
        User sender = userRepository.findByUsername(senderUsername).orElseThrow();

        MessageDTO saved = messageService.sendMessage(
                sender.getId(),
                dto.getReceiverId(),
                dto.getContent()
        );

  
        return ResponseEntity.ok(saved);
    }

    /* ------------------- 📌 2. Get conversation ------------------- */
    @GetMapping("/chat")
    public ResponseEntity<List<MessageDTO>> getChat(@RequestParam Long chatWithId, Principal principal) {
        String currentUsername = principal.getName();
        User current = userRepository.findByUsername(currentUsername).orElseThrow();

        List<MessageDTO> dtos = messageService.getConversation(current.getId(), chatWithId);
        

        return ResponseEntity.ok(dtos);
    }

    /* ------------------- 📌 3. Get all chat users ------------------- */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllChatUsers(Principal principal) {
        String currentUsername = principal.getName();
        User current = userRepository.findByUsername(currentUsername).orElseThrow();

        List<UserDTO> userDTOs = messageService.getAllChatUsers(current.getId())
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }

    /* ------------------- 📌 4. Get user details ------------------- */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);


        return userOptional.map(user -> ResponseEntity.ok(new UserDTO(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /* ------------------- 📌 5. Update message status ------------------- */
    @PutMapping("/{messageId}/status")
    public ResponseEntity<MessageDTO> updateMessageStatus(
            @PathVariable Long messageId,
            @RequestParam MessageStatus status
    ) {
        MessageDTO updated = messageService.updateMessageStatus(messageId, status);

        return ResponseEntity.ok(updated);
    }

    /* ------------------- 📌 6. Soft delete message ------------------- */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId, Principal principal) {
        String currentUsername = principal.getName();
        User current = userRepository.findByUsername(currentUsername).orElseThrow();

        messageService.deleteMessage(messageId, current.getId());

        return ResponseEntity.noContent().build();
    }
}
