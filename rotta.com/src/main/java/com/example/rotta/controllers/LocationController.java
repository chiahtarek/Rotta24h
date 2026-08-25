package com.example.rotta.controllers;

import java.security.Principal;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.example.rotta.dto.LocationDTO;
import com.example.rotta.models.User;
import com.example.rotta.repositories.UserRepository;
import com.example.rotta.services.LocationService;

import jakarta.transaction.Transactional;

@Controller
public class LocationController {

    @Autowired
    private LocationService locationService;

    @MessageMapping("/location.update")
    @Transactional
    public void updateLocation(@Payload LocationDTO dto, Principal principal) {
        Integer userId = Integer.valueOf(principal.getName());
        locationService.updateLocation(userId, dto.latitude(), dto.longitude(), Instant.now());
    }
}
