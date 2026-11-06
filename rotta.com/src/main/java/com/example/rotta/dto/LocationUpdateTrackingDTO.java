package com.example.rotta.dto;

public record LocationUpdateTrackingDTO(Integer helpRequestId, Integer userId, String userName, String address, Double distanceInMeters) {
    
}
