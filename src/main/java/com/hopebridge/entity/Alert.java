package com.hopebridge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_is_read", columnList = "is_read")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 USER (who receives alert)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    // 📝 MESSAGE (main content)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    // 🔔 TYPE (for UI styling)
    @Column(length = 50, nullable = false)
    private String alertType;

    // 🏢 OPTIONAL (can remove if you want ultra simple)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orphanage_id")
    private Orphanage orphanage;

    // ✅ READ STATUS
    @Column(nullable = false)
    private Boolean isRead = false;

    // 📅 CREATED TIME
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ================= HELPER METHODS =================

    public void markAsRead() {
        this.isRead = true;
    }

    public String getAlertClass() {
        return switch (alertType) {
            case "LOW_FUNDS" -> "alert-warning";
            case "DONATION" -> "alert-success";
            case "SYSTEM" -> "alert-danger";
            default -> "alert-secondary";
        };
    }

    public String getAlertIcon() {
        return switch (alertType) {
            case "LOW_FUNDS" -> "fa-exclamation-triangle";
            case "DONATION" -> "fa-gift";
            case "SYSTEM" -> "fa-bell";
            default -> "fa-info-circle";
        };
    }
}