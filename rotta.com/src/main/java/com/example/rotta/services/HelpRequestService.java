package com.example.rotta.services;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import com.example.rotta.repositories.LocationRepository;
import com.example.rotta.repositories.RiderRepository;
import com.example.rotta.repositories.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class HelpRequestService {

    private static final double RAIO_KM = 5.0;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HelpRequestRepository helpRequestRepository;

    @Autowired
    private LocationService locationService; // corrigir depois;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    RiderRepository riderRepository;

    private Map<Integer, Set<Integer>> notifiedUsers = new ConcurrentHashMap<>();

    public HelpRequest save(HelpRequestDTO dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Rider rider = riderRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Rider not found"));

        LocalDateTime now = LocalDateTime.now();
        HelpRequest helpRequest = new HelpRequest(rider, now, dto.latitude(), dto.longitude(), dto.problemType(),
                dto.description());
        helpRequestRepository.save(helpRequest);

        List<User> nearbyUsers = locationRepository.findNearbyOnlineByRole(dto.latitude(), dto.longitude(), RAIO_KM,
                user.getId(), UserRole.RIDER);

        Set<Integer> notifiedIds = ConcurrentHashMap.newKeySet();

        for (User riders : nearbyUsers) {
            Double distance = locationService.calculateDistanceInMeters(riders.getLatitude(), riders.getLongitude(),
                    dto.latitude(), dto.longitude());
            String distanceFormatted = distance < 1000 ? String.format("%.0f m", distance)
                    : String.format("%.1f km", distance / 1000);
            NotificationDTO notif = new NotificationDTO(helpRequest.getId(), "Novo pedido de ajuda", "NEW_REQUEST",
                    user.getFullName() + " precisa de ajuda: " + dto.problemType(), dto.latitude(), dto.longitude(),
                    distanceFormatted);

            messagingTemplate.convertAndSendToUser(riders.getId().toString(), "/queue/notifications", notif);
            System.out.println("user login e id: " +riders.getLogin() +riders.getFullName() +riders.getId());
            notifiedIds.add(riders.getId());
        }

        notifiedUsers.put(helpRequest.getId(), notifiedIds);

        return helpRequest;

    }

    @Transactional
    public boolean acceptHelpRequest(Integer requestId, Integer helperId) {
        User helperRef = entityManager.getReference(User.class, helperId);
        Integer updated = helpRequestRepository.acceptIfAvailable(requestId, helperRef);

        if (updated == 0) {
            NotificationDTO tooLate = new NotificationDTO(requestId, "Indisponível", "TOO_LATE",
                    "Esse pedido já foi atendido por outro usuário.", null, null, null);
            messagingTemplate.convertAndSendToUser(helperId.toString(), "/queue/notifications", tooLate);
            return false;
        }

        HelpRequest helpRequest = helpRequestRepository.findById(requestId).orElseThrow();
        User helper = userRepository.findById(helperId).orElseThrow();

        Integer requesterId = helpRequest.getRider().getUser().getId();
        NotificationDTO notifyRequester = new NotificationDTO(requestId, "Pedido aceito", "ACCEPTED",
                helper.getFullName() + " está a caminho.", null, null, null);
        messagingTemplate.convertAndSendToUser(requesterId.toString(), "/queue/notifications", notifyRequester);

        Set<Integer> notified = notifiedUsers.getOrDefault(requestId, Set.of());
        NotificationDTO cancel = new NotificationDTO(requestId, "Indisponível", "CANCELLED",
                "Esse pedido já foi atendido por outro usuário.", null, null, null);

        for (Integer uid : notified) {
            if (!uid.equals(helperId)) {
                messagingTemplate.convertAndSendToUser(uid.toString(), "/queue/notifications", cancel);
            }
        }

        notifiedUsers.remove(requestId);
        return true;
    }
}
