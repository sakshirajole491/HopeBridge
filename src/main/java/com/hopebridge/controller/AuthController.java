package com.hopebridge.controller;

import com.hopebridge.entity.User;
import com.hopebridge.service.OrphanageService;
import com.hopebridge.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OrphanageService orphanageService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {

        try {
            String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;

            User user = userService.registerUser(username, email, password, fullName, phone, roleWithPrefix);

            if (roleWithPrefix.equals("ROLE_ORPHANAGE")) {
                orphanageService.createOrphanage(
                        user.getId(),
                        "Default Name",
                        "",
                        "",
                        "",
                        "",
                        BigDecimal.ZERO,
                        "",
                        "",
                        "",
                        ""
                );
            }

            redirectAttributes.addFlashAttribute("success",
                    "Registration successful! Please login.");

            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}