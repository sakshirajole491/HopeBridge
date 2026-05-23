package com.hopebridge.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations", indexes = {
        @Index(name = "idx_donor_id", columnList = "donor_id"),
        @Index(name = "idx_orphanage_id", columnList = "orphanage_id"),
        @Index(name = "idx_donation_date", columnList = "donation_date"),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 DONOR
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    // 🏢 ORPHANAGE (ONLY ONE SYSTEM)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "orphanage_id", nullable = false)
    private Orphanage orphanage;

    // 💰 AMOUNT
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // 🎯 PURPOSE (shown in receipt/dashboard)
    @Column(length = 150)
    private String purpose;

    // 📝 DESCRIPTION
    @Column(columnDefinition = "TEXT")
    private String description;

    // 🧾 TYPE (for reports)
    @Column(length = 50)
    private String donationType;

    // 💳 PAYMENT INFO (basic only)
    @Column(length = 50)
    private String paymentMethod;

    @Column(unique = true, length = 255)
    private String transactionId;

    @Column(length = 20)
    private String panNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(unique = true)
    private String receiptNumber;

    // ✅ STATUS
    @Column(length = 50)
    private String status;

    // 📅 TIMESTAMP
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime donationDate;


    // ================= HELPER METHODS =================

    public String getDonorName() {
        return donor != null ? donor.getUser().getDisplayName() : "Unknown";
    }

    public String getOrphanageName() {
        return orphanage != null ? orphanage.getName() : "Unknown";
    }
}