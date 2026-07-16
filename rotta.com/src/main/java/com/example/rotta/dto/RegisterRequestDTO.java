package com.example.rotta.dto;

import com.example.rotta.roles.UserRole;

public record RegisterRequestDTO(String fullName, String login, String password, UserRole role, String driverLicense, String workShopName, String speciality, String brand, String model, String year, String licensePlate) {
}
