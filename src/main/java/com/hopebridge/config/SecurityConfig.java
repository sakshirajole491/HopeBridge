package com.hopebridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider) throws Exception {

        http
                .authenticationProvider(authenticationProvider)

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/", "/home", "/about", "/contact",
                                "/register", "/login",
                                "/css/**", "/js/**", "/images/**", "/webjars/**"
                        ).permitAll()

                        .requestMatchers("/donor/**").hasRole("DONOR")

                        .requestMatchers("/orphanage/**").hasRole("ORPHANAGE")

                        .requestMatchers("/dashboard").authenticated()

                        .anyRequest().authenticated()
                )

                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {

                            String role = authentication.getAuthorities()
                                    .iterator()
                                    .next()
                                    .getAuthority();

                            if (role.equals("ROLE_DONOR")) {
                                response.sendRedirect("/donor/dashboard");
                            } else if (role.equals("ROLE_ORPHANAGE")) {
                                response.sendRedirect("/orphanage/dashboard");
                            }
                        })
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )

                .rememberMe(remember -> remember
                        .key("hopebridge")
                        .tokenValiditySeconds(604800)
                )

                .sessionManagement(session -> session
                        .sessionFixation(sessionFixation ->
                                sessionFixation.migrateSession())
                );

        return http.build();
    }
}