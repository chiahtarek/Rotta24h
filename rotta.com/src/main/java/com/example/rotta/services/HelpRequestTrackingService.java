package com.example.rotta.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import com.example.rotta.dto.HelpRequestTrackingDTO;
import com.example.rotta.dto.LocationUpdateTrackingDTO;
import com.example.rotta.enums.HelpRequestStatus;
import com.example.rotta.models.HelpRequest;
import com.example.rotta.models.Rider;
import com.example.rotta.models.User;
import com.example.rotta.repositories.HelpRequestRepository;
import com.example.rotta.repositories.UserRepository;

@Service
public class HelpRequestTrackingService {

    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;

    private final LocationService locationService;
    private final UserAddressService addressService;

    private final SimpMessagingTemplate messagingTemplate;

    private static final List<HelpRequestStatus> ACTIVE_STATUSES
            = List.of(
                    HelpRequestStatus.ACCEPTED,
                    HelpRequestStatus.ARRIVED
            );

    public HelpRequestTrackingService(
            HelpRequestRepository helpRequestRepository,
            UserRepository userRepository,
            LocationService locationService,
            UserAddressService addressService,
            SimpMessagingTemplate messagingTemplate) {

        this.helpRequestRepository = helpRequestRepository;
        this.userRepository = userRepository;
        this.locationService = locationService;
        this.addressService = addressService;
        this.messagingTemplate = messagingTemplate;
    }


    // DADOS INICIAIS DA TELA

    @Transactional(readOnly = true)
    public HelpRequestTrackingDTO getTracking(Integer requestId) {

        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(()
                        -> new RuntimeException("Solicitação não encontrada"));

        if (!ACTIVE_STATUSES.contains(request.getStatus())) {
            throw new RuntimeException(
                    "Solicitação não está disponível para acompanhamento"
            );
        }

        User helper = request.getHelper();

        Rider rider = request.getRider();

        if (helper == null || rider == null) {
            throw new RuntimeException(
                    "Helper ou Rider não encontrado na solicitação"
            );
        }

        User riderUser = userRepository.findById(rider.getUser().getId())
                .orElseThrow(()
                        -> new RuntimeException("Usuário do Rider não encontrado"));

        HelpRequestTrackingDTO dto = new HelpRequestTrackingDTO(request.getId(), helper.getId(), helper.getFullName(), getAddress(helper),
                rider.getId(), rider.getUser().getId(), rider.getUser().getFullName(), getAddress(rider.getUser()), calculateDistance(helper, riderUser), request.getStatus());

        return dto;
    }


    // RECEBE ATUALIZAÇÃO DE LOCALIZAÇÃO

    @Transactional(readOnly = true)
    public void publishUserLocation(Integer userId) {

        Map<Integer, HelpRequest> requests = new LinkedHashMap<>();

        List<HelpRequest> helperRequests
                = helpRequestRepository.findByHelper_IdAndStatusIn(
                        userId,
                        ACTIVE_STATUSES
                );

        helperRequests.forEach(request
                -> requests.put(request.getId(), request)
        );

        List<HelpRequest> riderRequests
                = helpRequestRepository.findByRider_UserIdAndStatusIn(
                        userId,
                        ACTIVE_STATUSES
                );

        riderRequests.forEach(request
                -> requests.put(request.getId(), request)
        );

        // Publica somente nas solicitações relacionadas
        for (HelpRequest request : requests.values()) {

            publishTrackingUpdate(request, userId);
        }
    }

    // MONTA E PUBLICA A ATUALIZAÇÃO

    private void publishTrackingUpdate(
            HelpRequest request,
            Integer updatedUserId) {

        User helper = request.getHelper();

        Rider rider = request.getRider();

        if (helper == null || rider == null) {
            return;
        }

        User riderUser = userRepository.findById(rider.getUser().getId()).orElse(null);

        if (riderUser == null) {
            return;
        }

        boolean isHelper = helper.getId().equals(updatedUserId);

        boolean isRider = riderUser.getId().equals(updatedUserId);

        if (!isHelper && !isRider) {
            return;
        }

        User updatedUser = isHelper ? helper : riderUser;

        LocationUpdateTrackingDTO dto = new LocationUpdateTrackingDTO(request.getId(), updatedUser.getId(), updatedUser.getFullName(), getAddress(updatedUser), calculateDistance(helper, riderUser), request.getStatus());

        messagingTemplate.convertAndSend(
                "/topic/helprequest/" + request.getId(),
                dto
        );
    }

    @Transactional
    public void markArrived(Integer requestId, String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AccessDeniedException("Usuário não encontrado"));
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User helper = request.getHelper();
        if (helper == null || !helper.getId().equals(user.getId())) {
            throw new AccessDeniedException("Somente o prestador de ajuda pode confirmar a chegada.");
        }
        User rider = request.getRider().getUser();
        if (request.getStatus() == HelpRequestStatus.ARRIVED) {
            return;
        }
        if (request.getStatus() != HelpRequestStatus.ACCEPTED || helper == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação não está em atendimento");
        }
        Double distance = calculateDistance(helper, rider);
        if (distance == null || !Double.isFinite(distance) || distance < 0 || distance > 50) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Os usuários precisam estar a até 50 metros de distância.");
        }
        request.setStatus(HelpRequestStatus.ARRIVED);
        helpRequestRepository.save(request);
        publishTrackingUpdate(request, user.getId());
    }

    // ENDEREÇO

    private String getAddress(User user) {

        if (user.getLatitude() == null
                || user.getLongitude() == null) {
            return null;
        }

        return addressService.getUserAddress(
                user.getLatitude(),
                user.getLongitude()
        );
    }

    
    // DISTÂNCIA
    
    private Double calculateDistance(User helper, User rider) {

        if (helper.getLatitude() == null
                || helper.getLongitude() == null
                || rider.getLatitude() == null
                || rider.getLongitude() == null) {

            return null;
        }

        return locationService.calculateDistanceInMeters(
                helper.getLatitude(),
                helper.getLongitude(),
                rider.getLatitude(),
                rider.getLongitude()
        );
    }
}
