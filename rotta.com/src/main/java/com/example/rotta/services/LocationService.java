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

    public void updateLocation(Integer userId, Double lat, Double lng, Instant updateAt) {
        locationRepository.updateLocation(userId, lat, lng, updateAt);
    }

    public double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_METERS = 6_371_000;

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)+ Math.cos(lat1Rad)* Math.cos(lat2Rad)* Math.sin(deltaLon / 2)* Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

}
