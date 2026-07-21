package com.polyjobs.config;

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
        // Tiền tố cho các message từ server gửi về client
        config.enableSimpleBroker("/user");
        
        // Tiền tố cho các message từ client gửi lên server (ví dụ: @MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
        
        // Cấu hình tiền tố cho user-specific queues
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint mà client (SockJS) sẽ kết nối tới
        registry.addEndpoint("/ws-chat").withSockJS();
    }
}
