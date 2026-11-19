package com.example.rotta.dto;

import com.example.rotta.enums.HelpRequestStatus;

public record HelpRequestTrackingDTO(
        Integer helpRequestId, Integer helperId, Integer riderUserId,
        String otherName, String otherAddress,
        Double helperLatitude, Double helperLongitude,
        Double riderLatitude, Double riderLongitude,
        Double distanceInMeters, HelpRequestStatus status) {
}
