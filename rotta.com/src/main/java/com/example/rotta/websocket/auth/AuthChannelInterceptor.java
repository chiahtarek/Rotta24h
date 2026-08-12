package com.example.rotta.websocket.auth;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class);

        if (accessor != null &&
                StompCommand.CONNECT.equals(accessor.getCommand())) {

            Long userId = (Long) accessor
                    .getSessionAttributes()
                    .get("userId");

            if (userId != null) {
                accessor.setUser(
                        new StompPrincipal(userId.toString()));
            }
        }

        return message;
    }
}