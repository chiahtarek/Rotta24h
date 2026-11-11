package com.example.rotta.controllers;

import java.security.Principal;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.rotta.dto.HelpRequestTrackingDTO;
import com.example.rotta.models.HelpRequest;
import com.example.rotta.models.User;
import com.example.rotta.repositories.HelpRequestRepository;
import com.example.rotta.repositories.UserRepository;
import com.example.rotta.services.HelpRequestTrackingService;

@Controller
@RequestMapping("/helprequest")
public class HelpRequestTrackingController {

    private HelpRequestTrackingService trackingService;
    private HelpRequestRepository helpRequestRepository;
    private UserRepository userRepository;

    public HelpRequestTrackingController(
            HelpRequestTrackingService trackingService,
            HelpRequestRepository helpRequestRepository, 
            UserRepository userRepository) {

        this.trackingService = trackingService;
        this.helpRequestRepository = helpRequestRepository;
        this.userRepository = userRepository; 
    }

    @PostMapping("/details/{id}/arrived")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void arrived(@PathVariable Integer id, Principal principal) {
        trackingService.markArrived(id, principal.getName());
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Integer id,Principal principal, Model model) {

        User loggedUser = userRepository.findByLogin(principal.getName())
                .orElseThrow(()
                        -> new RuntimeException("Usuário não encontrado"));

        Integer userId = loggedUser.getId();

        HelpRequest request = helpRequestRepository.findById(id)
                .orElseThrow(()
                        -> new RuntimeException("Solicitação não encontrada"));

        boolean isHelper
                = request.getHelper() != null
                && request.getHelper().getId().equals(userId);

        boolean isRider
                = request.getRider() != null
                && request.getRider().getUser().getId().equals(userId);

        if (!isHelper && !isRider) {
            throw new AccessDeniedException(
                    "Você não participa desta solicitação."
            );
        }

        HelpRequestTrackingDTO tracking
                = trackingService.getTracking(id);

        model.addAttribute("tracking", tracking);
        model.addAttribute("isHelper", isHelper);

        return "helprequest/details";
    }
}
