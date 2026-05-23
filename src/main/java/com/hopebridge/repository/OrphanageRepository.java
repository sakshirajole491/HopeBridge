package com.hopebridge.repository;

import com.hopebridge.entity.Orphanage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrphanageRepository extends JpaRepository<Orphanage, Long> {
    Optional<Orphanage> findByAdminId(Long adminId);
    List<Orphanage> findByIsVerifiedTrue();
    List<Orphanage> findByCountry(String country);
    List<Orphanage> findByCity(String city);
    Optional<Orphanage> findByRegistrationNumber(String registrationNumber);

    @Query("SELECT o FROM Orphanage o WHERE o.availableFunds < o.requiredFunds * 0.3")
    List<Orphanage> findOrphanagesWithLowFunds();

    @Query("SELECT COUNT(o) FROM Orphanage o")
    Long countAllOrphanages();

    @Query("SELECT COUNT(o) FROM Orphanage o WHERE o.isVerified = true")
    Long countVerifiedOrphanages();

    @Query("SELECT SUM(o.requiredFunds) FROM Orphanage o")
    BigDecimal getTotalRequiredFunds();

    @Query("SELECT SUM(o.availableFunds) FROM Orphanage o")
    BigDecimal getTotalAvailableFunds();
}
