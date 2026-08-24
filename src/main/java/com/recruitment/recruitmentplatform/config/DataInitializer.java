package com.recruitment.recruitmentplatform.config;

import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    // ==========================================
    // READ VALUES FROM application.yaml
    // ==========================================
    @Value("${app.security.admin-email}")
    private String adminEmail;

    @Value("${app.security.admin-password}")
    private String adminPassword;

    @Value("${app.security.ldap-email}")
    private String ldapEmail;

    @Value("${app.security.ldap-username}")
    private String ldapUsername;

    @Value("${app.security.ldap-password}")
    private String ldapPassword;

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

                User admin = new User();

                admin.setName("System Administrator");
                admin.setEmail(adminEmail); // ✅ جاية من الـ yaml
                admin.setPassword(passwordEncoder.encode(adminPassword)); // ✅ جاية من الـ yaml
                admin.setRole("ADMIN");

                userRepository.save(admin);

                System.out.println("========================================");
                System.out.println("ADMIN ACCOUNT CREATED");
                System.out.println("Email: " + adminEmail);
                System.out.println("Password: " + adminPassword);
                System.out.println("========================================");
            }

            /*
             * ==========================================
             * CREATE LDAP USER IN MYSQL
             * ==========================================
             */
            if (!userRepository.existsByEmail(ldapEmail)) {

                User ldapUser = new User();

                ldapUser.setName("LDAP User");
                ldapUser.setEmail(ldapEmail); // ✅ جاية من الـ yaml

                /*
                 * This password is not used for LDAP authentication.
                 * LDAP validates: ldapuser / Ldap@123
                 */
                ldapUser.setPassword(passwordEncoder.encode(ldapPassword)); // ✅ جاية من الـ yaml
                ldapUser.setRole("HR");

                userRepository.save(ldapUser);

                System.out.println("========================================");
                System.out.println("LDAP APPLICATION USER CREATED");
                System.out.println("Email: " + ldapEmail);
                System.out.println("LDAP Username: " + ldapUsername);
                System.out.println("LDAP Password: " + ldapPassword);
                System.out.println("Role: HR");
                System.out.println("========================================");
            }
        };
    }
}