package com.instagramclone.config;

import com.instagramclone.dto.MessageDTO;
import com.instagramclone.model.Message;
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
        Long senderId = messageDTO.getSenderId(); // <--- Use directly

        Message saved = messageService.sendMessage(senderId, messageDTO.getReceiverId(), messageDTO.getContent());

        MessageDTO dto = new MessageDTO(saved);

        messagingTemplate.convertAndSend("/topic/messages/" + senderId, dto);
        messagingTemplate.convertAndSend("/topic/messages/" + messageDTO.getReceiverId(), dto);
    }

}
