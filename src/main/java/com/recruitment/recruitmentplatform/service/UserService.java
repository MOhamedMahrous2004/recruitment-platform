package com.recruitment.recruitmentplatform.service;

import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    /*
     * ==========================================
     * CANDIDATE REGISTRATION
     * ==========================================
     *
     * Public registration always creates
     * a CANDIDATE.
     *
     * Users cannot choose ADMIN, HR,
     * or INTERVIEWER from the public
     * registration page.
     */
    public User registerCandidate(
            User user) {

        /*
         * Check if email already exists.
         */
        if (userRepository.existsByEmail(
                user.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        /*
         * Public registration is always
         * a CANDIDATE.
         */
        user.setRole(
                "CANDIDATE"
        );

        /*
         * Encrypt password.
         */
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        /*
         * Save user.
         */
        return userRepository.save(
                user
        );
    }

    /*
     * ==========================================
     * ADMIN CREATE USER
     * ==========================================
     *
     * ADMIN can create:
     *
     * ADMIN
     * HR
     * INTERVIEWER
     * CANDIDATE
     */
    public User createUserByAdmin(
            User user) {

        /*
         * Check duplicate email.
         */
        if (userRepository.existsByEmail(
                user.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        /*
         * Get selected role.
         */
        String role =
                user.getRole();

        /*
         * Validate role.
         *
         * ADMIN is allowed to create
         * all four application roles.
         */
        if (!"ADMIN".equalsIgnoreCase(role)
                && !"HR".equalsIgnoreCase(role)
                && !"INTERVIEWER".equalsIgnoreCase(role)
                && !"CANDIDATE".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "Admin can only create ADMIN, HR, INTERVIEWER or CANDIDATE users"
            );
        }

        /*
         * Normalize role.
         */
        user.setRole(
                role.toUpperCase()
        );

        /*
         * Encrypt password.
         */
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        /*
         * Save user.
         */
        return userRepository.save(
                user
        );
    }
}