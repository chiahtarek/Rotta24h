package com.example.rotta.services;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.management.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.rotta.dto.HelpRequestDTO;
import com.example.rotta.dto.NotificationDTO;
import com.example.rotta.enums.UserRole;
import com.example.rotta.models.HelpRequest;
import com.example.rotta.models.Rider;
import com.example.rotta.models.User;
import com.example.rotta.repositories.HelpRequestRepository;
import com.example.rotta.repositories.RiderRepository;
import com.example.rotta.repositories.UserRepository;

@Service
public class HelpRequestService {

    private static final double RAIO_KM = 5.0;

    @Autowired
    SimpMessagingTemplate messagingTemplate; 

    @Autowired
    private HelpRequestRepository helpRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RiderRepository riderRepository;

    public HelpRequest save(HelpRequestDTO dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Rider rider = riderRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Rider not found"));

        HelpRequest helpRequest = new HelpRequest();
        LocalDateTime now = LocalDateTime.now();
        helpRequest.setDateTime(now);
        helpRequest.setRider(rider);
        helpRequest.setLatitude(dto.latitude());
        helpRequest.setLongitude(dto.longitude());
        helpRequest.setProblemType(dto.problemType());
        helpRequest.setDescription(dto.description());

        List<User> nearbyUsers = userRepository.findNearbyOnlineByRole(dto.latitude(), dto.longitude(), RAIO_KM, user.getId(), UserRole.RIDER);

        NotificationDTO notif = new NotificationDTO("Novo pedido de ajuda", user.getFullName() + " precisa de ajuda: " + 
        dto.problemType(), dto.latitude(), dto.longitude()); 

        for (User riders : nearbyUsers) {
            System.out.println(riders.getLogin());
            messagingTemplate.convertAndSendToUser(riders.getId().toString(), "/queue/notifications", notif);
        }

        return helpRequestRepository.save(helpRequest);

    }
}
