package com.example.rotta.services;

import java.security.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.rotta.dto.HelpRequestDTO;
import com.example.rotta.models.HelpRequest;
import com.example.rotta.models.Rider;
import com.example.rotta.models.User;
import com.example.rotta.repositories.HelpRequestRepository;
import com.example.rotta.repositories.RiderRepository;

@Service
public class HelpRequestService {

    @Autowired
    private HelpRequestRepository helpRequestRepository;

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

        return helpRequestRepository.save(helpRequest);

    }
}
