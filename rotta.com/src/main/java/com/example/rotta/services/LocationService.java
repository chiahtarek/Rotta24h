package com.example.rotta.services;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rotta.enums.UserRole;
import com.example.rotta.models.User;
import com.example.rotta.repositories.LocationRepository;
import com.example.rotta.repositories.UserRepository;

@Service
public class LocationService {
    
    @Autowired
    LocationRepository locationRepository;

    public List<User> findNearOnline(Double lat, Double lng, Double radius, Integer excludeId, UserRole role) {
        return locationRepository.findNearbyOnlineByRole(lat, lng, radius, excludeId, role);
    }

    public void updateLocation(Integer userId, Double lat, Double lng, Instant updateAt){
        locationRepository.updateLocation(userId, lat, lng, updateAt); 
    }
}
