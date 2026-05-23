package com.hopebridge.repository;

import com.hopebridge.entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
    Optional<Donor> findByUserId(Long userId);
    List<Donor> findByIsVerifiedTrue();
    List<Donor> findTop10ByOrderByTotalDonationsDesc();

    @Query("SELECT d FROM Donor d WHERE d.totalDonations > :amount ORDER BY d.totalDonations DESC")
    List<Donor> findTopDonorsByAmount(@Param("amount") BigDecimal amount);

    @Query("SELECT COUNT(d) FROM Donor d")
    Long countAllDonors();

    @Query("SELECT SUM(d.totalDonations) FROM Donor d")
    BigDecimal getTotalDonationsAmount();

    @Query("SELECT AVG(d.totalDonations) FROM Donor d")
    BigDecimal getAverageDonationAmount();
}
