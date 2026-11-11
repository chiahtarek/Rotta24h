package com.example.rotta.dto;

import com.example.rotta.enums.HelpRequestStatus;

public record LocationUpdateTrackingDTO(Integer helpRequestId, Integer userId, String userName, String address, Double distanceInMeters, HelpRequestStatus status) {
    
}
