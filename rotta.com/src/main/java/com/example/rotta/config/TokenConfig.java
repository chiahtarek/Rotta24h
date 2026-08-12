package com.example.rotta.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.rotta.models.User;
import com.example.rotta.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenConfig {

    @Autowired
    private UserRepository userRepository; 

    private static final String SECRET = "01234567890123456789012345678901";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getLogin())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86_400_000)) // 24 hours
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public Integer extractUserIdByLogin(String login){
        String userName = extractUsername(login); 
        User user = userRepository.findByLogin(userName).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return user.getId(); 
    }
}