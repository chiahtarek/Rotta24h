package com.example.rotta.dto;

import com.example.rotta.roles.UserRole;

public record RegisterRequestDTO(String login, String password, UserRole role) {
}
