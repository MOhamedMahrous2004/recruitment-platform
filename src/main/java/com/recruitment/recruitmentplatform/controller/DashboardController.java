package com.recruitment.recruitmentplatform.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    /*
     * ================================
     * DASHBOARD
     * ================================
     */
    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        /*
         * Get logged-in user's email.
         */
        String email =
                authentication.getName();

        /*
         * Get user's role.
         */
        String role =
                authentication
                        .getAuthorities()
                        .stream()
                        .findFirst()
                        .map(authority ->
                                authority.getAuthority()
                        )
                        .orElse("");

        /*
         * Remove ROLE_ prefix.
         */
        if (role.startsWith(
                "ROLE_"
        )) {

            role =
                    role.substring(5);
        }

        /*
         * Send information to Thymeleaf.
         */
        model.addAttribute(
                "email",
                email
        );

        model.addAttribute(
                "role",
                role
        );

        /*
         * ================================
         * ROLE DASHBOARD
         * ================================
         */
        switch (role) {

            case "ADMIN":
                return "admin-dashboard";

            case "HR":
                return "hr-dashboard";

            case "CANDIDATE":
                return "candidate-dashboard";

            case "INTERVIEWER":
                return "interviewer-dashboard";

            default:
                return "dashboard";
        }
    }
}