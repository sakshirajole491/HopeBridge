package com.hopebridge.service;

import com.hopebridge.entity.Donation;
import com.hopebridge.entity.Donor;
import com.hopebridge.entity.Orphanage;
import com.hopebridge.entity.User;
import com.hopebridge.repository.OrphanageRepository;
import com.hopebridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrphanageService {

    private final OrphanageRepository orphanageRepository;
    private final UserRepository userRepository;
    private final AlertService alertService;

    public Orphanage createOrphanage(Long userId, String name, String location, String country, String city, 
                                     String registrationNumber, BigDecimal requiredFunds, String description,
                                     String contactPerson, String contactEmail, String contactPhone) {
        log.info("Creating orphanage for user: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (orphanageRepository.findByAdminId(userId).isPresent()) {
            throw new IllegalArgumentException("Orphanage profile already exists for this user");
        }

        Orphanage orphanage = Orphanage.builder()
            .admin(user)
            .name(name)
            .location(location)
            .country(country)
            .city(city)
            .registrationNumber(registrationNumber)
            .requiredFunds(requiredFunds)
            .availableFunds(BigDecimal.ZERO)
            .description(description)
            .contactPerson(contactPerson)
            .contactEmail(contactEmail)
            .contactPhone(contactPhone)
            .isVerified(false)
            .build();

        return orphanageRepository.save(orphanage);
    }

    public Optional<Orphanage> findByUserId(Long userId) {
        return orphanageRepository.findByAdminId(userId);
    }

    public Optional<Orphanage> findById(Long id) {
        return orphanageRepository.findById(id);
    }

    public List<Orphanage> findAllVerifiedOrphanages() {
        return orphanageRepository.findByIsVerifiedTrue();
    }

    public List<Orphanage> findOrphanagesWithLowFunds() {
        return orphanageRepository.findOrphanagesWithLowFunds();
    }

    public Orphanage updateOrphanage(Long id, String name, String location, String description,
                                     String contactPerson, String contactEmail, String contactPhone,
                                     BigDecimal requiredFunds) {
        log.info("Updating orphanage: {}", id);
        Orphanage orphanage = orphanageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Orphanage not found"));

        orphanage.setName(name);
        orphanage.setLocation(location);
        orphanage.setDescription(description);
        orphanage.setContactPerson(contactPerson);
        orphanage.setContactEmail(contactEmail);
        orphanage.setContactPhone(contactPhone);
        orphanage.setRequiredFunds(requiredFunds);

        return orphanageRepository.save(orphanage);
    }

    public void addFunds(Long orphanageId, BigDecimal amount) {
        log.info("Adding funds: {} to orphanage: {}", amount, orphanageId);
        Orphanage orphanage = orphanageRepository.findById(orphanageId)
            .orElseThrow(() -> new IllegalArgumentException("Orphanage not found"));

        orphanage.addFunds(amount);
        orphanageRepository.save(orphanage);
        alertService.createDonationAlert(
                orphanageId,
                "Funds added: ₹" + amount
        );

        alertService.createLowFundsAlert(orphanageId);
    }

    public List<Orphanage> findByCountry(String country) {
        return orphanageRepository.findByCountry(country);
    }

    public List<Orphanage> findByCity(String city) {
        return orphanageRepository.findByCity(city);
    }

    public Long countAllOrphanages() {
        return orphanageRepository.countAllOrphanages();
    }

    public Long countVerifiedOrphanages() {
        return orphanageRepository.countVerifiedOrphanages();
    }

    public BigDecimal getTotalRequiredFunds() {
        BigDecimal total = orphanageRepository.getTotalRequiredFunds();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalAvailableFunds() {
        BigDecimal total = orphanageRepository.getTotalAvailableFunds();
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<Orphanage> findAll() {
        return orphanageRepository.findAll();
    }

    public Orphanage save(Orphanage orphanage) {
        if (orphanage == null) {
            throw new IllegalArgumentException("Orphanage cannot be null");
        }
        return orphanageRepository.save(orphanage);
    }
}
