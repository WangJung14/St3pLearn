package com.tommy.identity.infrastructure.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tommy.identity.domain.entity.Role;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // Generate Access Token
    public String generateAccessToken(UUID userId, String username, Set<Role> roles) {
        Map<String, Object> extraClaims = new HashMap<>();
        if (roles != null && !roles.isEmpty()) {
            String roleName = roles.stream()
                    .map(Role::getName)
                    .filter(name -> "ADMIN".equalsIgnoreCase(name) || "TEACHER".equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(roles.iterator().next().getName());
            extraClaims.put("role", roleName);
        } else {
            extraClaims.put("role", "STUDENT");
        }
        return buildToken(extraClaims, userId, username, jwtExpiration);
    }

    // Generate Refresh Token
    public String generateRefreshToken(UUID userId, String username) {
        return buildToken(new HashMap<>(), userId, username, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UUID userId, String username, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userId.toString()) // save userId into subject
                .claim("username", username) // save username
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    // Get UserId from Token String
    public UUID extractUserId(String token) {
        String subject = extractAllClaims(token).getSubject();
        return UUID.fromString(subject);
    }

    // Check token is valid ?
    public boolean isTokenValid(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
