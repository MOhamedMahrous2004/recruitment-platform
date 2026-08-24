package com.recruitment.recruitmentplatform.controller;

import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /*
     * ================================
     * CANDIDATE REGISTER PAGE
     * ================================
     */
    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute(
                "user",
                new User()
        );

        return "register";
    }

    /*
     * ================================
     * REGISTER CANDIDATE
     * ================================
     */
    @PostMapping("/register")
    public String register(
            @ModelAttribute("user") User user,
            Model model) {

        try {

            /*
             * Public registration ALWAYS
             * creates a CANDIDATE.
             */
            userService.registerCandidate(user);

            return "redirect:/login";

        } catch (RuntimeException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "register";
        }
    }

    /*
     * ================================
     * LOGIN PAGE
     * ================================
     */
    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }
}