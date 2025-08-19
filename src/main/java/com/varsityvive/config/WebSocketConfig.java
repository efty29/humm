package com.varsityvive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory broker for specific destinations
        config.enableSimpleBroker("/queue", "/topic");
        
        // Set prefix for application destination (messages sent from client)
        config.setApplicationDestinationPrefixes("/app");
        
        // Set prefix for user-specific destinations
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint
        registry.addEndpoint("/ws-chat")
                // For production, replace "*" with your frontend origin(s)
                .setAllowedOriginPatterns("http://localhost:*", "https://yourdomain.com") 
                // Enable SockJS fallback options
                .withSockJS()
                // Configure SockJS options
                .setSupressCors(true);
    }
}