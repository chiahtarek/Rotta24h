package com.example.rotta.dto;

public record NotificationDTO(Integer helpRequestId, String title, String type, String message, Double latitude, Double longitude, String distance) {}
