package com.hopebridge.service;

import com.hopebridge.entity.Donor;
import com.hopebridge.entity.Orphanage;
import com.hopebridge.entity.User;
import com.hopebridge.repository.DonorRepository;
import com.hopebridge.repository.OrphanageRepository;
import com.hopebridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.AstLessThan;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DonorRepository donorRepository;
    private final OrphanageRepository orphanageRepository;
    private final AlertService alertService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        if (!user.getEnabled()) {
            log.warn("User account is disabled: {}", username);
            throw new UsernameNotFoundException("User account is disabled");
        }

        log.debug("User role from database: '{}'", user.getRole());

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        log.debug("Created authority: '{}'", authority.getAuthority());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }

    public User registerUser(String username, String email, String password, String fullName, String phone, String role) {
        log.info("Registering new user: {} with role: {}", username, role);

        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            log.error("Username already exists: {}", username);
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            log.error("Email already exists: {}", email);
            throw new IllegalArgumentException("Email already exists");
        }

        // IMPORTANT: Ensure role has ROLE_ prefix
        String roleToStore = role;
        if (!role.startsWith("ROLE_")) {
            roleToStore = "ROLE_" + role;
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .phone(phone)
                .role(roleToStore)  // Store with ROLE_ prefix
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Create corresponding profile based on role
        if (roleToStore.equals("ROLE_DONOR")) {
            Donor donor = new Donor();
            donor.setUser(savedUser);
            donorRepository.save(donor);
            log.info("Donor profile created for user: {}", username);
        } else if (roleToStore.equals("ROLE_ORPHANAGE")) {
            Orphanage orphanage = new Orphanage();
            orphanage.setAdmin(savedUser);
            orphanageRepository.save(orphanage);
            log.info("Orphanage profile created for user: {}", username);
        }

        log.info("User registered successfully: {} with ID: {}, role: {}",
                username, savedUser.getId(), savedUser.getRole());

        alertService.createSystemAlert(
                savedUser.getId(),
                "Welcome to HopeBridge 🎉"
        );

        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, String fullName, String phone, String email) {
        log.info("Updating user: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        user.setFullName(fullName);
        user.setPhone(phone);
        user.setEmail(email);

        return userRepository.save(user);
    }

    public boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public User changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!validatePassword(user, oldPassword)) {
            log.warn("Invalid old password for user: {}", userId);
            throw new IllegalArgumentException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public void enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        log.info("User enabled: {}", id);
    }

    public void disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
        log.info("User disabled: {}", id);
    }

    public Long countUsersByRole(String role) {
        return userRepository.countByRole(role);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
