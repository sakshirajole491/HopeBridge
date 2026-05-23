package com.hopebridge.repository;

import com.hopebridge.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonorId(Long donorId);
    List<Donation> findByOrphanageId(Long orphanageId);
    List<Donation> findByOrphanageIdOrderByDonationDateDesc(Long orphanageId);
    List<Donation> findByDonationDate(LocalDateTime date);
    List<Donation> findTop5ByOrphanageIdOrderByDonationDateDesc(Long orphanageId);

    @Query("SELECT d FROM Donation d WHERE d.donor.id = :donorId ORDER BY d.donationDate DESC")
    List<Donation> findDonationsByDonorOrderByDate(@Param("donorId") Long donorId);

    @Query("SELECT d FROM Donation d WHERE d.donationDate >= :startDate AND d.donationDate <= :endDate ORDER BY d.donationDate DESC")
    List<Donation> findDonationsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(d) FROM Donation d")
    Long countAllDonations();

    @Query("SELECT SUM(d.amount) FROM Donation d")
    BigDecimal getTotalDonationsAmount();

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.donationDate >= :date")
    BigDecimal getDonationsAmountSince(@Param("date") LocalDateTime date);

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE DATE(d.donationDate) = CURDATE()")
    BigDecimal getTodaysDonationsAmount();

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.orphanage.id = :orphanageId")
    Long countDonationsByOrphanage(@Param("orphanageId") Long orphanageId);


    @Query("SELECT d FROM Donation d ORDER BY d.donationDate DESC LIMIT 10")
    List<Donation> findRecentDonations();

    @Query("SELECT d FROM Donation d JOIN FETCH d.donor")
    List<Donation> findAllWithDonor();

    @Query("SELECT d FROM Donation d JOIN FETCH d.donor dn JOIN FETCH dn.user")
    List<Donation> findAllWithDonorAndUser();

    @Query("""
    SELECT d FROM Donation d
    JOIN FETCH d.donor donor
    JOIN FETCH donor.user
    WHERE d.orphanage.id = :orphanageId
    ORDER BY d.donationDate DESC
""")
    List<Donation> findByOrphanageIdWithDonorUser(Long orphanageId);
}