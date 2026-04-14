package com.platform.support.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple in-memory broker for broadcasting to "/topic" and "/queue"
        registry.enableSimpleBroker("/topic", "/queue");
        // Set prefix for client-to-server messages (e.g., /app/chat.sendMessage)
        registry.setApplicationDestinationPrefixes("/app");
        // Set prefix for user-specific messages (e.g., /user/queue/notifications)
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint that clients will connect to
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Adjust for production
                .withSockJS(); // Enable SockJS fallback options
    }
}