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
        Integer userId = Integer.valueOf(principal.getName()); 
        userRepository.updateLocation(userId, dto.latitude(), dto.longitude(), Instant.now());
    }
}
