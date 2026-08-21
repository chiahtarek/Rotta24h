package com.example.rotta.websocket.auth;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.rotta.config.TokenConfig;
import com.example.rotta.models.User;
import com.example.rotta.repositories.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private final TokenConfig tokenConfig; // sua classe atual de validação de token

    @Autowired
    UserRepository userRepository;

    public AuthHandshakeInterceptor(TokenConfig tokenConfig) {
        this.tokenConfig = tokenConfig;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            Cookie[] cookies = httpRequest.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JWT".equals(cookie.getName())) {
                        String token = cookie.getValue();
                        try {
                            String userLogin = tokenConfig.extractUsername(token);
                            User user = userRepository.findByLogin(userLogin).orElse(null);
                            if (user == null) {
                                return false;
                            }
                            attributes.put("userId", user.getId());
                            return true;
                        } catch (Exception e) {
                            return false; 
                        }
                    }
                }
            }
        }
        return false; 
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
    }
}
