package com.hopebridge.service;

import com.hopebridge.entity.Donation;
import com.hopebridge.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final OrphanageRepository orphanageRepository;
    private final UserRepository userRepository;

    public Map<String, Object> getAdminDashboardStats() {
        log.debug("Fetching admin dashboard statistics");
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalDonations", donationRepository.getTotalDonationsAmount());
        stats.put("todaysDonations", donationRepository.getTodaysDonationsAmount());
        stats.put("totalDonors", donorRepository.countAllDonors());
        stats.put("totalOrphanages", orphanageRepository.countAllOrphanages());
        stats.put("verifiedOrphanages", orphanageRepository.countVerifiedOrphanages());
        stats.put("totalDonationCount", donationRepository.countAllDonations());

        return stats;
    }

    public Map<String, Object> getDonorDashboardStats(Long donorId) {
        log.debug("Fetching donor dashboard statistics for donor: {}", donorId);
        Map<String, Object> stats = new HashMap<>();

        stats.put("donations", donationRepository.findDonationsByDonorOrderByDate(donorId));
        stats.put("recentDonations", donationRepository.findDonationsByDonorOrderByDate(donorId).stream()
            .limit(5)
            .toList());

        return stats;
    }

    public Map<String, Object> getOrphanageDashboardStats(Long orphanageId) {
        log.debug("Fetching orphanage dashboard statistics for orphanage: {}", orphanageId);
        Map<String, Object> stats = new HashMap<>();


        stats.put("donations", donationRepository.findByOrphanageId(orphanageId));
        stats.put("recentDonations", donationRepository.findByOrphanageId(orphanageId).stream()
            .limit(5)
            .toList());


        return stats;
    }

    public BigDecimal getTotalDonationsAmount() {
        BigDecimal total = donationRepository.getTotalDonationsAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getAverageDonationAmount() {
        return donorRepository.getAverageDonationAmount();
    }

    public Long getTotalDonorsCount() {
        return donorRepository.countAllDonors();
    }

    public Long getTotalOrphanagesCount() {
        return orphanageRepository.countAllOrphanages();
    }

}
