package com.example.rotta.dto;

public record HelpRequestTrackingDTO(Integer helpRequestId, Integer helperId, String helperName, String helperAddress, Integer riderId, Integer riderUserId, String riderName, String riderAddress, Double distanceInMeters) {

}
