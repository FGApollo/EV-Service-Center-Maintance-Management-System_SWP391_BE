package com.example.Ev.System.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Quan trọng: bật SockJS nếu FE dùng SockJS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Client gửi vào /app/**
        registry.setApplicationDestinationPrefixes("/app");
        // Server broadcast ra /topic/**
        registry.enableSimpleBroker("/topic");
        // Nếu scale nhiều instance, cân nhắc dùng RabbitMQ (enableStompBrokerRelay)
    }
}
