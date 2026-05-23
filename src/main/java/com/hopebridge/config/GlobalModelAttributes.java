package com.hopebridge.config;

import com.hopebridge.entity.Orphanage;
import com.hopebridge.entity.User;
import com.hopebridge.service.OrphanageService;
import com.hopebridge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UserService userService;
    private final OrphanageService orphanageService;

    @ModelAttribute("orphanage")
    public Orphanage addOrphanage(Authentication authentication) {
        if (authentication == null) return null;

        return userService.findByUsername(authentication.getName())
                .flatMap(user -> orphanageService.findByUserId(user.getId()))
                .orElse(null);
    }
}