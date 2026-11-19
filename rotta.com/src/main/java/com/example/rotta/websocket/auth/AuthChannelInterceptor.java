package com.example.rotta.websocket.auth;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);

        if (accessor != null &&
                StompCommand.CONNECT.equals(accessor.getCommand())) {

            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

            Integer userId = sessionAttributes != null ? (Integer) sessionAttributes.get("userId") : null;

            if (userId != null) {
                accessor.setUser(new StompPrincipal(userId.toString()));
            } else {
                System.out.println(">>> AVISO: CONNECT sem userId na sessão!");
            }
        }

        if (accessor != null) {
            String destination = accessor.getDestination();
            if (destination != null && (destination.startsWith("/queue/helprequest/")
                    || (StompCommand.SEND.equals(accessor.getCommand())
                        && destination.startsWith("/user/")
                        && destination.contains("/queue/helprequest/")))) {
                throw new AccessDeniedException("Destino de acompanhamento reservado.");
            }
        }

        return message;
    }
}