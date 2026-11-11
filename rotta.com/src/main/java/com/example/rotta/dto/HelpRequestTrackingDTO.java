package com.example.rotta.dto;

import com.example.rotta.enums.HelpRequestStatus;

public record HelpRequestTrackingDTO(Integer helpRequestId, Integer helperId, String helperName, String helperAddress, Integer riderId, Integer riderUserId, String riderName, String riderAddress, Double distanceInMeters, HelpRequestStatus status) {

}
