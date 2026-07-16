package com.example.rotta.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.rotta.models.Motorcycle;
import com.example.rotta.models.Rider;
import com.example.rotta.models.User;
import com.example.rotta.repositories.MotorcycleRepository;
import com.example.rotta.repositories.RiderRepository;

@Service
public class MotorcycleService {

    @Autowired
    RiderRepository riderRepository;

    @Autowired
    MotorcycleRepository motorcycleRepository;

    public Motorcycle save(Motorcycle motorcycle) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Rider rider = riderRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Rider not found"));
        motorcycle.setRider(rider);

        return motorcycleRepository.save(motorcycle);
    }
}
