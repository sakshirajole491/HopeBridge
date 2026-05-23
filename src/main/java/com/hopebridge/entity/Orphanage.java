package com.hopebridge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orphanages", indexes = {
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_location", columnList = "location"),
        @Index(name = "idx_country", columnList = "country"),
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_is_verified", columnList = "is_verified")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orphanage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "admin_id", nullable = false, unique = true)
    private User admin;

    // 🏢 BASIC INFO
    @Column(length = 150, nullable = false)
    private String name;

    @Column(length = 255)
    private String location;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    @Column(length = 100, unique = true)
    private String registrationNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 📞 CONTACT
    @Column(length = 100)
    private String contactPerson;

    @Column(length = 100)
    private String contactEmail;

    @Column(length = 20)
    private String contactPhone;

    // 💰 FUND MANAGEMENT (CORE 🔥)
    @Column(precision = 12, scale = 2)
    private BigDecimal requiredFunds = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal availableFunds;

    @Column(name = "total_funds_received", precision = 12, scale = 2)
    private BigDecimal totalFundsReceived = BigDecimal.ZERO;

    // ✅ VERIFICATION (USED IN SERVICE)
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column
    private LocalDateTime verificationDate;

    // 🖼 OPTIONAL UI
    @Column(length = 255)
    private String logoUrl;

    // 🕒 AUDIT
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ================= HELPER METHODS =================

    public void addFunds(BigDecimal amount) {
        if (this.availableFunds == null) {
            this.availableFunds = BigDecimal.ZERO;
        }

        if (this.totalFundsReceived == null) {
            this.totalFundsReceived = BigDecimal.ZERO;
        }

        this.availableFunds = this.availableFunds.add(amount);
        this.totalFundsReceived = this.totalFundsReceived.add(amount);
    }

    public BigDecimal getRemainingFunds() {
        return requiredFunds.subtract(availableFunds).max(BigDecimal.ZERO);
    }

    public double getFundProgress() {
        if (requiredFunds.compareTo(BigDecimal.ZERO) == 0) return 0;

        return availableFunds
                .divide(requiredFunds, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public String getFullLocation() {
        if (city != null && country != null) {
            return city + ", " + country;
        } else if (city != null) {
            return city;
        } else if (country != null) {
            return country;
        }
        return location;
    }

    public BigDecimal getTotalFundsReceived() {
        return totalFundsReceived != null ? totalFundsReceived : BigDecimal.ZERO;
    }
}