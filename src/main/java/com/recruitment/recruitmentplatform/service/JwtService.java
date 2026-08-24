package com.recruitment.recruitmentplatform.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "RecruitmentPlatformSecretKeyForJWTAuthentication2026";

    // Access Token: 15 minutes
    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 15;

    // Refresh Token: 7 days
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // ==================== ACCESS TOKEN ====================
    public String generateAccessToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(userDetails.getUsername(), null);
    }

    // ==================== REFRESH TOKEN ====================
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    public LocalDateTime getRefreshTokenExpiryDate() {
        return LocalDateTime.now().plusDays(7);
    }

    // ==================== TOKEN EXTRACTION ====================
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ فقط دالة واحدة isTokenExpired (public)
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ==================== VALIDATION ====================
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return isAccessTokenValid(token, userDetails); // same logic
    }
}