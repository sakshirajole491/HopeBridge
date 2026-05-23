package com.hopebridge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_role", columnList = "role")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔐 AUTHENTICATION
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    // 👤 PROFILE (USED IN DASHBOARD)
    @Column(length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String profileImageUrl;

    // 🔑 ROLE (IMPORTANT FOR SECURITY)
    @Column(nullable = false, length = 50)
    private String role; // ROLE_ADMIN / ROLE_DONOR

    // 🔒 ACCOUNT STATUS
    @Column(nullable = false)
    private Boolean enabled = true;

    // 🕒 AUDIT
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ================= HELPER METHODS =================

    public String getDisplayName() {
        return (fullName != null && !fullName.isBlank()) ? fullName : username;
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(this.role);
    }

    public boolean isDonor() {
        return "ROLE_DONOR".equals(this.role);
    }
}