package com.hopebridge.service;

import com.hopebridge.entity.Alert;
import com.hopebridge.entity.Orphanage;
import com.hopebridge.entity.User;
import com.hopebridge.repository.AlertRepository;
import com.hopebridge.repository.OrphanageRepository;
import com.hopebridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final OrphanageRepository orphanageRepository;

    // ================= CREATE ALERT =================

    public void createDonationAlert(Long orphanageId, String message) {
        log.info("Creating donation alert for orphanage: {}", orphanageId);

        Orphanage orphanage = orphanageRepository.findById(orphanageId)
                .orElseThrow(() -> new IllegalArgumentException("Orphanage not found"));

        User admin = orphanage.getAdmin(); // 👈 IMPORTANT (based on your entity)

        Alert alert = Alert.builder()
                .user(admin)
                .orphanage(orphanage)
                .message(message)
                .alertType("DONATION")
                .isRead(false)
                .build();

        alertRepository.save(alert);
    }

    public void createLowFundsAlert(Long orphanageId) {
        log.info("Checking low funds for orphanage: {}", orphanageId);

        Orphanage orphanage = orphanageRepository.findById(orphanageId)
                .orElseThrow(() -> new IllegalArgumentException("Orphanage not found"));

        if (orphanage.getRemainingFunds().compareTo(java.math.BigDecimal.ZERO) > 0) {

            User admin = orphanage.getAdmin();

            String message = "Funds are low. Required: ₹" +
                    orphanage.getRequiredFunds() +
                    ", Received: ₹" +
                    orphanage.getTotalFundsReceived();

            Alert alert = Alert.builder()
                    .user(admin)
                    .orphanage(orphanage)
                    .message(message)
                    .alertType("LOW_FUNDS")
                    .isRead(false)
                    .build();

            alertRepository.save(alert);
        }
    }

    public void createSystemAlert(Long userId, String message) {
        log.info("Creating system alert for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Alert alert = Alert.builder()
                .user(user)
                .message(message)
                .alertType("SYSTEM")
                .isRead(false)
                .build();

        alertRepository.save(alert);
    }

    // ================= FETCH =================

    public List<Alert> getUserAlerts(Long userId) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Alert> getUnreadAlerts(Long userId) {
        return alertRepository.findByUserIdAndIsReadFalse(userId);
    }

    // ================= UPDATE =================

    public void markAsRead(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found"));

        alert.markAsRead();
        alertRepository.save(alert);
    }

    public void markAllAsRead(Long userId) {
        List<Alert> alerts = alertRepository.findByUserIdAndIsReadFalse(userId);

        alerts.forEach(Alert::markAsRead);
        alertRepository.saveAll(alerts);
    }

    // ================= DELETE (OPTIONAL) =================

    public void deleteAlert(Long alertId) {
        alertRepository.deleteById(alertId);
    }
}