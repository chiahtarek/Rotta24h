package com.example.rotta.dto;

import com.example.rotta.enums.HelpRequestStatus;

public record LocationUpdateTrackingDTO(
        Integer helpRequestId, Integer userId, String address,
        Double latitude, Double longitude, Double distanceInMeters, HelpRequestStatus status) {
}
