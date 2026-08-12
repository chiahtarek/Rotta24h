package com.example.rotta.websocket;

import java.security.Principal;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.rotta.models.User;
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
        User swapUser = userRepository.findByLogin(principal.getName()).orElseThrow(() -> new RuntimeException("Usuário não encontrado")); 

        if (principal != null) {
            Long userId = swapUser.getId().longValue();
            userRepository.markOffline(userId);
        }
    }
}