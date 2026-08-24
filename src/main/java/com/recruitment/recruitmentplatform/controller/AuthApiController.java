package com.recruitment.recruitmentplatform.controller;

import com.recruitment.recruitmentplatform.dto.LoginRequest;
import com.recruitment.recruitmentplatform.dto.LoginResponse;
import com.recruitment.recruitmentplatform.dto.RefreshRequest;
import com.recruitment.recruitmentplatform.dto.RefreshResponse;
import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.UserRepository;
import com.recruitment.recruitmentplatform.service.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthApiController(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String username = request.getEmail();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        User user = findApplicationUser(username);

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        LocalDateTime refreshTokenExpiry = jwtService.getRefreshTokenExpiryDate();

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(refreshTokenExpiry);
        userRepository.save(user);

        return new LoginResponse(accessToken, refreshToken, user.getEmail(), user.getRole());
    }

    // ==================== REFRESH TOKEN ====================
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        try {
            // 1. Extract username
            String email = jwtService.extractUsername(refreshToken);

            // 2. Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 3. Validate JWT signature and expiration
            if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 4. Check if refresh token exists in DB and is not expired
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

            if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (user.getRefreshTokenExpiry() == null || user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 5. Generate new access token
            String newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());

            // 6. Return new tokens
            return ResponseEntity.ok(new RefreshResponse(newAccessToken, refreshToken));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private User findApplicationUser(String username) {
        User user = userRepository.findByEmail(username).orElse(null);
        if (user != null) return user;

        if ("ldapuser".equalsIgnoreCase(username)) {
            return userRepository.findByEmail("ldapuser@recruitment.com")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "LDAP application user not found"));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Application user not found");
    }
}