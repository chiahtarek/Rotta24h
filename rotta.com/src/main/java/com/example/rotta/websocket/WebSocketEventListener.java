package com.example.rotta.websocket;

import java.security.Principal;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.rotta.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Component
public class WebSocketEventListener {

    private final UserRepository userRepository;

    public WebSocketEventListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    @Transactional
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();

        if (principal == null) {
            return; // sessão nunca autenticou (ex: probe de negociação do SockJS) - nada a fazer
        }

        try {
            Integer userId = Integer.valueOf(principal.getName()); // já é o userId, não precisa buscar por login
            userRepository.markOffline(userId);
        } catch (NumberFormatException e) {
            // não deveria acontecer, mas evita quebrar o listener por segurança
        }
    }
}