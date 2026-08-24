package com.recruitment.recruitmentplatform.controller;

import com.recruitment.recruitmentplatform.dto.LoginRequest;
import com.recruitment.recruitmentplatform.dto.LoginResponse;
import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.UserRepository;
import com.recruitment.recruitmentplatform.service.JwtService;

import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthApiController(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        String username =
                request.getEmail();

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            request.getPassword()
                    )
            );

        } catch (AuthenticationException e) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        User user =
                findApplicationUser(username);

        String token =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole()
        );
    }

    private User findApplicationUser(
            String username) {

        User user =
                userRepository
                        .findByEmail(username)
                        .orElse(null);

        if (user != null) {
            return user;
        }

        if ("ldapuser".equalsIgnoreCase(username)) {

            return userRepository
                    .findByEmail(
                            "ldapuser@recruitment.com"
                    )
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.UNAUTHORIZED,
                                    "LDAP application user not found"
                            )
                    );
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Application user not found"
        );
    }
}