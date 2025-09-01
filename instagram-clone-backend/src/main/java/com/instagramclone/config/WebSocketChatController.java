package com.instagramclone.config;

import com.instagramclone.dto.MessageDTO;
import com.instagramclone.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

   

    @MessageMapping("/chat.send")
    public void handleChatMessage(MessageDTO messageDTO) {
        Long senderId = messageDTO.getSenderId();
        Long receiverId = messageDTO.getReceiverId();

        // ✅ Call service using the correct method signature
        MessageDTO savedDto = messageService.sendMessage(
                senderId,
                receiverId,
                messageDTO.getContent()
        );

        // ✅ Notify both sender and receiver
        messagingTemplate.convertAndSend("/topic/messages/" + senderId, savedDto);
        messagingTemplate.convertAndSend("/topic/messages/" + receiverId, savedDto);
    }



}
