package com.hopebridge.config;

import com.hopebridge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;

    @Bean
    CommandLineRunner initUsers() {
        return args -> {

            // Donor 1
            if (!userService.existsByUsername("donor1")) {
                userService.registerUser(
                        "donor1",
                        "donor1@hopebridge.org",
                        "donor123",
                        "Sakshi Rajole",
                        "+1234567893",
                        "ROLE_DONOR"
                );
            }

            // Orphanage 1
            if (!userService.existsByUsername("orphanage1")) {
                userService.registerUser(
                        "orphanage1",
                        "orphanage1@hopebridge.org",
                        "orphanage123",
                        "Sunshine Orphanage",
                        "+1234567891",
                        "ROLE_ADMIN"
                );
            }


        };
    }
}