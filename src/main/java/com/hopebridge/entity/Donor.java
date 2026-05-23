package com.hopebridge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donors", indexes = {
        @Index(name = "idx_total_donations", columnList = "total_donations"),
        @Index(name = "idx_impact_score", columnList = "impact_score"),
        @Index(name = "idx_is_verified", columnList = "is_verified")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 LINK WITH USER (LOGIN SYSTEM)
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 💰 CORE DASHBOARD DATA
    @Column(name = "total_donations", precision = 15, scale = 2)
    private BigDecimal totalDonations = BigDecimal.ZERO;

    @Column(name = "impact_score")
    private Integer impactScore = 0;

    // 🌍 PROFILE INFO (USED IN UI)
    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 255)
    private String profileImageUrl;

    // ✅ TRUST / VERIFICATION
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    // 🕒 AUDIT
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ================= BUSINESS LOGIC =================

    public void addDonation(BigDecimal amount) {
        if (this.totalDonations == null) {
            this.totalDonations = BigDecimal.ZERO;
        }

        this.totalDonations = this.totalDonations.add(amount);

        if (this.impactScore == null) {
            this.impactScore = 0;
        }

        this.impactScore += 10;
    }

    public String getFullLocation() {
        if (city != null && country != null) {
            return city + ", " + country;
        } else if (city != null) {
            return city;
        } else if (country != null) {
            return country;
        }
        return "Not specified";
    }

    public String getDonorName() {
        return user != null ? user.getDisplayName() : "Anonymous";
    }
}