package com.hopebridge.service;

import com.hopebridge.entity.Donation;
import com.hopebridge.entity.Donor;
import com.hopebridge.entity.User;
import com.hopebridge.repository.DonationRepository;
import com.hopebridge.repository.DonorRepository;
import com.hopebridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DonorService {

    private final DonorRepository donorRepository;
    private final UserRepository userRepository;
    private final DonationRepository donationRepository;

    public Donor createDonor(Long userId, String country, String city, String bio) {
        log.info("Creating donor profile for user: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (donorRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Donor profile already exists for this user");
        }

        Donor donor = Donor.builder()
            .user(user)
            .country(country)
            .city(city)
            .bio(bio)
            .totalDonations(BigDecimal.ZERO)
            .impactScore(0)
            .isVerified(false)
            .build();

        return donorRepository.save(donor);
    }

    public Optional<Donor> findByUserId(Long userId) {
        return donorRepository.findByUserId(userId);
    }

    public Optional<Donor> findById(Long id) {
        return donorRepository.findById(id);
    }

    public List<Donor> findAllVerifiedDonors() {
        return donorRepository.findByIsVerifiedTrue();
    }

    public List<Donor> findTopDonors() {
        return donorRepository.findTop10ByOrderByTotalDonationsDesc();
    }

    public Donor updateDonor(Long id, String country, String city, String bio, String profileImageUrl) {
        log.info("Updating donor: {}", id);
        Donor donor = donorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Donor not found"));

        donor.setCountry(country);
        donor.setCity(city);
        donor.setBio(bio);
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            donor.setProfileImageUrl(profileImageUrl);
        }

        return donorRepository.save(donor);
    }

    public void addDonationAmount(Long donorId, BigDecimal amount) {
        log.info("Adding donation amount: {} to donor: {}", amount, donorId);
        Donor donor = donorRepository.findById(donorId)
            .orElseThrow(() -> new IllegalArgumentException("Donor not found"));

        donor.addDonation(amount);
        donorRepository.save(donor);
    }

    public void verifyDonor(Long id) {
        Donor donor = donorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Donor not found"));
        donor.setIsVerified(true);
        donorRepository.save(donor);
        log.info("Donor verified: {}", id);
    }

    public BigDecimal getTotalDonationsAmount() {
        BigDecimal total = donorRepository.getTotalDonationsAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getAverageDonationAmount() {
        BigDecimal average = donorRepository.getAverageDonationAmount();
        return average != null ? average : BigDecimal.ZERO;
    }

    public Long countAllDonors() {
        return donorRepository.countAllDonors();
    }
    @Transactional
    public void addDonation(Long donorId, BigDecimal amount) {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new IllegalArgumentException("Donor not found"));

        donor.addDonation(amount);
        donorRepository.save(donor);
    }
    public List<Donor> findDonorsByOrphanage(Long orphanageId) {
        return donationRepository.findByOrphanageId(orphanageId)
                .stream()
                .map(Donation::getDonor)
                .distinct()
                .toList();
    }
}
