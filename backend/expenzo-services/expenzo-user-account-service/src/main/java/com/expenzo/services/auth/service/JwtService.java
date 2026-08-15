package com.expenzo.services.auth.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

    private final String JWT_SIGN_SECRET_KEY = "SEpTXzg4MzkyX1NKUldRTl8yMzIzMl9TREpBU0JKQV9KSlNIRDg4ODlfSkpTVVM=";

    // This method should accept the user details and return the JWT token back
    public String generateAccessToken(String userId) {
        return Jwts.builder()
            .subject(userId)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
            .signWith(getSignKey())
            .compact();
    }

    public String generateRefreshToken(String userId) {
        return Jwts.builder()
            .subject(userId)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
            .signWith(getSignKey())
            .compact();
    }

    // This method should accept the token and extract the user-id from it
    public String extractUserId(String token) {
        Claims claims = this.getClaims(token);
        return claims.getSubject();
    }

    public boolean isValid(String token) {
        return true;
    }

    public String refreshToken(String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
            this.isValid(refreshToken);
            String userName = this.extractUserId(refreshToken);
            return generateAccessToken(userName);
        } else {
            throw new RuntimeException("Failed to refresh the token");
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SIGN_SECRET_KEY));
    }
}
