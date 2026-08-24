package com.recruitment.recruitmentplatform.controller;

import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }


    /*
     * ==========================================
     * ADMIN MANAGE USERS PAGE
     * ==========================================
     */
    @GetMapping("/users")
    public String usersPage(Model model) {

        model.addAttribute(
                "user",
                new User()
        );

        return "admin-users";
    }


    /*
     * ==========================================
     * CREATE USER
     * ==========================================
     */
    @PostMapping("/users/create")
    public String createUser(
            @ModelAttribute("user") User user,
            Model model) {

        try {

            /*
             *  Admin creates ADMIN, HR or CANDIDATE.
             */
            userService.createUserByAdmin(user);

            model.addAttribute(
                    "success",
                    "User created successfully."
            );

            /*
             * Empty form after creation.
             */
            model.addAttribute(
                    "user",
                    new User()
            );

            return "admin-users";

        } catch (RuntimeException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "admin-users";
        }
    }
}