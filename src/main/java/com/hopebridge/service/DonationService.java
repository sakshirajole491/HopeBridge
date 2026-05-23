package com.hopebridge.service;

import com.hopebridge.entity.Donation;
import com.hopebridge.entity.Donor;
import com.hopebridge.entity.Orphanage;
import com.hopebridge.repository.DonationRepository;
import com.hopebridge.repository.DonorRepository;
import com.hopebridge.repository.OrphanageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final OrphanageRepository orphanageRepository;
    private final AlertService alertService;
    private final OrphanageService orphanageService;

    // ✅ MAIN METHOD (USE THIS ONLY)
    public Donation createDonation(Long donorId,
                                   Long orphanageId,
                                   BigDecimal amount,
                                   String purpose,
                                   String description,
                                   String panNumber,
                                   String address,
                                   String receiptNo) {

        log.info("Creating donation of {} from donor {} to orphanage {}", amount, donorId, orphanageId);

        // Fetch donor
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new IllegalArgumentException("Donor not found"));

        // Fetch orphanage
        Orphanage orphanage = orphanageRepository.findById(orphanageId)
                .orElseThrow(() -> new IllegalArgumentException("Orphanage not found"));

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        // Update donor totals
        donor.addDonation(amount);

        // Create donation entity
        Donation donation = Donation.builder()
                .donor(donor)
                .orphanage(orphanage)
                .amount(amount)
                .purpose(purpose)
                .description(description)
                .panNumber(panNumber)
                .address(address)
                .receiptNumber(receiptNo)

                .donationType("GENERAL_DONATION")
                .paymentMethod("ONLINE")
                .transactionId(UUID.randomUUID().toString())
                .status("COMPLETED")
                .donationDate(LocalDateTime.now())
                .build();

        // Save donation
        Donation saved = donationRepository.save(donation);

        // Update orphanage funds (merged logic from second function)
        if (saved.getOrphanage() != null) {
            BigDecimal currentFunds = orphanage.getAvailableFunds() != null
                    ? orphanage.getAvailableFunds()
                    : BigDecimal.ZERO;

            orphanage.setAvailableFunds(currentFunds.add(saved.getAmount()));
            orphanageService.save(orphanage);
        }

        // Save donor updates
        donorRepository.save(donor);

        // Alerts
        alertService.createDonationAlert(
                orphanage.getId(),
                "New donation received: ₹" + amount
        );

        alertService.createLowFundsAlert(orphanage.getId());

        return saved;
    }

    // ✅ FETCH METHODS (for dashboard)
    public List<Donation> findByDonorId(Long donorId) {
        return donationRepository.findDonationsByDonorOrderByDate(donorId);
    }

    public List<Donation> findByOrphanageId(Long orphanageId) {
        return donationRepository.findByOrphanageIdOrderByDonationDateDesc(orphanageId);
    }

    public List<Donation> findRecentDonations() {
        return donationRepository.findRecentDonations();
    }

    public BigDecimal getTotalDonationsAmount() {
        BigDecimal total = donationRepository.getTotalDonationsAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTodaysDonationsAmount() {
        BigDecimal today = donationRepository.getTodaysDonationsAmount();
        return today != null ? today : BigDecimal.ZERO;
    }

    public Long countAllDonations() {
        return donationRepository.countAllDonations();
    }

    public List<Donation> findTop5ByOrphanageIdOrderByDonationDateDesc(Long orphanageId) {
        return donationRepository.findTop5ByOrphanageIdOrderByDonationDateDesc(orphanageId);
    }
    public List<Donation> findByOrphanageIdWithDonorUser(Long orphanageId) {
        return donationRepository.findByOrphanageIdWithDonorUser(orphanageId);
    }

    public java.util.Optional<Donation> findById(Long id) {
        return donationRepository.findById(id);
    }
}