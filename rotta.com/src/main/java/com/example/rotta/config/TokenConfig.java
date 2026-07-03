package com.example.rotta.config;

import java.time.Instant;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.rotta.models.User;

@Component
public class TokenConfig {

    private String secret = "secret";

    public String generateToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create().withClaim("userId", user.getId()).
                withSubject(user.getLogin())
                .withExpiresAt(Instant.now().
                plusSeconds(86699)).
                withIssuedAt(Instant.now()).
                sign(algorithm);

    }

    
}
