package com.example.rotta.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import com.example.rotta.models.User;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.rotta.services.HelpRequestTrackingService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.example.rotta.controllers")
public class ActiveHelpRequestAdvice {

    private final HelpRequestTrackingService trackingService;

    public ActiveHelpRequestAdvice(HelpRequestTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @ModelAttribute("activeHelpRequestIds")
    public List<Integer> activeHelpRequestIds(Principal principal, HttpServletRequest request) {
        String detailsPrefix = request.getContextPath() + "/helprequest/details/";
        if (principal == null || request.getRequestURI().startsWith(detailsPrefix)) {
            return List.of();
        }
        return trackingService.getActiveRequestIds(principal.getName());
    }
}
