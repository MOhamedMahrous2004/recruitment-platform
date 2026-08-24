package com.recruitment.recruitmentplatform.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    /*
     * Secret key used to sign and verify JWT tokens.
     *
     * IMPORTANT:
     * In a real production application,
     * this key should be stored in environment variables.
     */
    private static final String SECRET_KEY =
            "RecruitmentPlatformSecretKeyForJWTAuthentication2026";

    /*
     * Token validity:
     * 24 hours
     */
    private static final long EXPIRATION_TIME =
            1000L * 60 * 60 * 24;

    /**
     * Create the SecretKey used by JWT.
     */
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generate JWT token using email and role.
     */
    public String generateToken(
            String email,
            String role) {

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generate JWT token using UserDetails.
     *
     * This method can be useful later
     * when integrating Spring Security UserDetails.
     */
    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract username/email from JWT token.
     */
    public String extractUsername(String token) {

        Claims claims = extractAllClaims(token);

        return claims.getSubject();
    }

    /**
     * Extract role from JWT token.
     */
    public String extractRole(String token) {

        Claims claims = extractAllClaims(token);

        return claims.get("role", String.class);
    }

    /**
     * Extract expiration date from JWT token.
     */
    public Date extractExpiration(String token) {

        Claims claims = extractAllClaims(token);

        return claims.getExpiration();
    }

    /**
     * Extract all claims from JWT.
     *
     * Uses the modern JJWT 0.12.x API.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if the token is expired.
     */
    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    /**
     * Validate JWT token against the logged-in user.
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        final String username =
                extractUsername(token);

        return username.equals(
                userDetails.getUsername()
        ) && !isTokenExpired(token);
    }
}