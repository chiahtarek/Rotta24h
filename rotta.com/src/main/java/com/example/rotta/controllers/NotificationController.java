package com.example.rotta.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.rotta.dto.NotificationDTO;
import com.example.rotta.dto.NotifyRequestDTO;
import com.example.rotta.models.User;
import com.example.rotta.services.LocationService;
import com.example.rotta.services.UserService;

@Controller
public class NotificationController {

    @Autowired
    private LocationService locationService;

    @Autowired UserService userService; 

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notify.nearby")
    public void notifyNearby(@Payload NotifyRequestDTO req, Principal principal) {
        Integer userId = Integer.valueOf(principal.getName());
        User sender = userService.findById(userId);

        List<User> nearby = locationService.findNearOnline(
                sender.getLatitude(), sender.getLongitude(), req.radiusKm(), userId, sender.getRole());

        NotificationDTO notif = new NotificationDTO(
                "Pedido de ajuda",
                sender.getFullName() + " precisa de ajuda perto de você",
                sender.getLatitude(), sender.getLongitude());

        for (User u : nearby) {
            // "/user/{id}/queue/notifications" — Spring resolve o {id} pelo Principal da
            // sessão do destinatário
            messagingTemplate.convertAndSendToUser(u.getId().toString(), "/queue/notifications", notif);
        }
    }

}
