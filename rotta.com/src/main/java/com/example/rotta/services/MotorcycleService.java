package com.example.rotta.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.rotta.models.Motorcycle;
import com.example.rotta.models.Rider;
import com.example.rotta.models.User;
import com.example.rotta.repositories.MotorcycleRepository;
import com.example.rotta.repositories.RiderRepository;
import com.example.rotta.repositories.UserRepository;

@Service
public class MotorcycleService {

    @Autowired
    RiderRepository riderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MotorcycleRepository motorcycleRepository;

    public Motorcycle save(Motorcycle motorcycle) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Rider rider = riderRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Rider not found"));
        motorcycle.setRider(rider);

        return motorcycleRepository.save(motorcycle);
    }

    public List<Motorcycle> motorcyclesByLogin(String login) {
        User user = userRepository.findByLogin(login).orElseThrow();
        Rider rider = riderRepository.findByUser(user).orElseThrow();
        return motorcycleRepository.listById(rider.getId());
    }
}
