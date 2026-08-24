package com.recruitment.recruitmentplatform.config;

import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createInitialUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            /*
             * ==========================================
             * CREATE INITIAL ADMIN
             * ==========================================
             */
            boolean adminExists =
                    userRepository
                            .findAll()
                            .stream()
                            .anyMatch(user ->
                                    "ADMIN".equalsIgnoreCase(
                                            user.getRole()
                                    )
                            );

            if (!adminExists) {

                User admin =
                        new User();

                admin.setName(
                        "System Administrator"
                );

                admin.setEmail(
                        "admin@recruitment.com"
                );

                admin.setPassword(
                        passwordEncoder.encode(
                                "Admin@123"
                        )
                );

                admin.setRole(
                        "ADMIN"
                );

                userRepository.save(
                        admin
                );

                System.out.println(
                        "========================================"
                );

                System.out.println(
                        "ADMIN ACCOUNT CREATED"
                );

                System.out.println(
                        "Email: admin@recruitment.com"
                );

                System.out.println(
                        "Password: Admin@123"
                );

                System.out.println(
                        "========================================"
                );
            }

            /*
             * ==========================================
             * CREATE LDAP USER IN MYSQL
             * ==========================================
             *
             * LDAP authenticates the credentials.
             *
             * MySQL stores the application role.
             */
            if (!userRepository.existsByEmail(
                    "ldapuser@recruitment.com"
            )) {

                User ldapUser =
                        new User();

                ldapUser.setName(
                        "LDAP User"
                );

                ldapUser.setEmail(
                        "ldapuser@recruitment.com"
                );

                /*
                 * This password is not used
                 * for LDAP authentication.
                 *
                 * LDAP validates:
                 *
                 * ldapuser / Ldap@123
                 */
                ldapUser.setPassword(
                        passwordEncoder.encode(
                                "Ldap@123"
                        )
                );

                /*
                 * Give LDAP user HR role
                 * for demonstration.
                 */
                ldapUser.setRole(
                        "HR"
                );

                userRepository.save(
                        ldapUser
                );

                System.out.println(
                        "========================================"
                );

                System.out.println(
                        "LDAP APPLICATION USER CREATED"
                );

                System.out.println(
                        "Email: ldapuser@recruitment.com"
                );

                System.out.println(
                        "LDAP Username: ldapuser"
                );

                System.out.println(
                        "LDAP Password: Ldap@123"
                );

                System.out.println(
                        "Role: HR"
                );

                System.out.println(
                        "========================================"
                );
            }
        };
    }
}