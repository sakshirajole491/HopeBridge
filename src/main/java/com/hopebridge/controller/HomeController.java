package com.hopebridge.controller;

import com.hopebridge.entity.User;
import com.hopebridge.service.DonorService;
import com.hopebridge.service.OrphanageService;
import com.hopebridge.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final DonorService donorService;
    private final OrphanageService orphanageService;

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            String username = authentication.getName();
            userService.findByUsername(username).ifPresent(user -> {
                model.addAttribute("user", user);
            });
        }
        return "home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        return "redirect:/";
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {
        return "about";
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {
        return "contact";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        if (user.getRole().contains("ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (user.getRole().contains("DONOR")) {
            return "redirect:/donor/dashboard";
        } else if (user.getRole().contains("ORPHANAGE")) {
            return "redirect:/orphanage/dashboard";
        }

        return "redirect:/";
    }
}
