package com.platform.support.api;

import com.platform.support.application.dto.ChatMessageDTO;
import com.platform.support.application.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDto,
                            SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received message: {}", chatMessageDto);
        String sessionId = headerAccessor.getSessionId();
        chatService.sendMessage(UUID.fromString(sessionId), chatMessageDto);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessageDto) {
        log.info("User joined: {}", chatMessageDto.getSenderId());
        return chatMessageDto;
    }
}