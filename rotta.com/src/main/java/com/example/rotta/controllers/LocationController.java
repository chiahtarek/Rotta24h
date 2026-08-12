package com.example.rotta.controllers;

import java.security.Principal;
import java.time.Instant;
import java.util.Optional;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.example.rotta.dto.LocationDTO;
import com.example.rotta.models.User;
import com.example.rotta.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Controller
public class LocationController {

    private final UserRepository userRepository;

    public LocationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @MessageMapping("/location.update")
    @Transactional
    public void updateLocation(@Payload LocationDTO dto, Principal principal) {
        String username = principal.getName();
        User swapUser = userRepository.findByLogin(username).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        userRepository.updateLocation(swapUser.getId().longValue(), dto.latitude(), dto.longitude(), Instant.now());
    }
}
