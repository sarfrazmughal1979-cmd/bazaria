package com.platform.support.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.dto.PagedResponse;
import com.platform.core.exception.BusinessException;
import com.platform.core.security.SecurityUtils;
import com.platform.support.application.dto.ChatMessageDTO;
import com.platform.support.application.mapper.SupportMapper;
import com.platform.support.domain.model.ChatMessage;
import com.platform.support.domain.model.ChatSession;
import com.platform.support.domain.repository.ChatMessageRepository;
import com.platform.support.domain.repository.ChatSessionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final SupportMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final RestClientFactory restClientFactory;

    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;

    private ResilientRestClient iamRestClient;

    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
    }

    @Transactional
    public ChatSession startCustomerChat(UUID customerId) {
        // Check for existing active session
        var existing = sessionRepository.findByCustomerIdAndStatus(customerId, "ACTIVE");
        if (existing.isPresent()) {
            return existing.get();
        }

        ChatSession session = ChatSession.builder()
            .customerId(customerId)
            .status("ACTIVE")
            .startedAt(Instant.now())
            .build();

        return sessionRepository.save(session);
    }

    @Transactional
    public ChatSession startVendorChat(UUID vendorId) {
        var existing = sessionRepository.findByVendorIdAndStatus(vendorId, "ACTIVE");
        if (existing.isPresent()) {
            return existing.get();
        }

        ChatSession session = ChatSession.builder()
            .vendorId(vendorId)
            .status("ACTIVE")
            .startedAt(Instant.now())
            .build();

        return sessionRepository.save(session);
    }

    @Transactional
    public ChatMessage sendMessage(UUID sessionId, ChatMessageDTO messageDto) {
        UUID senderId = SecurityUtils.getCurrentUserId();
        String senderType = determineSenderType(senderId);

        ChatSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "Chat session not found"));

        // Verify user belongs to this session
        boolean authorized = (session.getCustomerId() != null && session.getCustomerId().equals(senderId)) ||
                            (session.getVendorId() != null && session.getVendorId().equals(senderId)) ||
                            (session.getAgentId() != null && session.getAgentId().equals(senderId)) ||
                            SecurityUtils.hasRole("ADMIN");

        if (!authorized) {
            throw new BusinessException("ACCESS_DENIED", "Not authorized to send message in this session");
        }

        ChatMessage chatMessage = ChatMessage.builder()
            .sessionId(sessionId)
            .senderId(senderId)
            .senderType(senderType)
            .message(messageDto.getMessage())
            .sentAt(Instant.now())
            .readAt(messageDto.getSentAt())
            .build();

        chatMessage = messageRepository.save(chatMessage);

        // Prepare response DTO for WebSocket
        ChatMessageDTO response = ChatMessageDTO.builder()
                .id(chatMessage.getId())
                .senderId(chatMessage.getSenderId())
                .recipientId(chatMessage.getRecipientId())
                .message(chatMessage.getMessage())
                .sentAt(chatMessage.getSentAt())
                .build();

        // Send via WebSocket to the specific recipient if present
        if (chatMessage.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipientId().toString(),
                    "/queue/messages",
                    response
            );
        }
        // Also broadcast to the session topic (all participants)
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, response);

        return chatMessage;
    }

    @Transactional
    public void markMessagesRead(UUID recipientId) {
        UUID readerId = SecurityUtils.getCurrentUserId();
        List<ChatMessage> unreadMessages = messageRepository.findByRecipientIdAndReadAtIsNull(recipientId);
        List<ChatMessage> toUpdate = unreadMessages.stream()
                .filter(msg -> msg.getRecipientId().equals(readerId))
                .peek(msg -> msg.setReadAt(Instant.now()))
                .collect(Collectors.toList());
        if (!toUpdate.isEmpty()) {
            messageRepository.saveAll(toUpdate);
        }
    }

    @Transactional
    public void assignAgentToSession(UUID sessionId, UUID agentId) {
        ChatSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "Chat session not found"));
        session.setAgentId(agentId);
        sessionRepository.save(session);
    }

    @Transactional
    public void closeSession(UUID sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "Chat session not found"));
        session.close();
        sessionRepository.save(session);
    }

    private String determineSenderType(UUID senderId) {
        if (SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("SUPPORT")) {
            return "AGENT";
        }
        if (SecurityUtils.hasRole("VENDOR")) {
            return "VENDOR";
        }
        return "CUSTOMER";
    }
}