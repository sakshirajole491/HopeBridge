package com.hopebridge.config;

import com.hopebridge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;

    @Value("${ADMIN_USERNAME}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_PHONE}")
    private String adminPhone;

    @Bean
    CommandLineRunner initUsers() {
        return args -> {

            if (!userService.existsByUsername(adminUsername)) {

                userService.registerUser(
                        adminUsername,
                        adminEmail,
                        adminPassword,
                        adminName,
                        adminPhone,
                        "ROLE_ADMIN"
                );

                System.out.println("Admin account created successfully.");
            }
        };
    }
}