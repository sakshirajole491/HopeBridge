package com.hopebridge.controller;

import com.hopebridge.entity.Donation;
import com.hopebridge.entity.Donor;
import com.hopebridge.entity.Orphanage;
import com.hopebridge.entity.User;
import com.hopebridge.repository.DonationRepository;
import com.hopebridge.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orphanage")
@Slf4j
@RequiredArgsConstructor
public class OrphanageController {

    private final OrphanageService orphanageService;
    private final DonationService donationService;
    private final DonationRepository donationRepository;
    private final UserService userService;
    private final DonorService donorService;
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String orphanageDashboard(Model model, Authentication authentication) {
        log.info("Orphanage dashboard accessed by: {}", authentication.getName());

        User user = userService.findByUsername(authentication.getName()).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        Orphanage orphanage = orphanageService.findByUserId(user.getId()).orElse(null);

        if (orphanage == null) {
            return "redirect:/register"; // or create orphanage page
        }

        List<Donation> donations =
                donationService.findByOrphanageIdWithDonorUser(orphanage.getId());

        long totalDonors = donations.stream()
                .filter(d -> d.getDonor() != null)
                .map(d -> d.getDonor().getId())
                .distinct()
                .count();

        BigDecimal totalFunds = donations.stream()
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orphanage.setAvailableFunds(totalFunds);
        orphanageService.save(orphanage);

        model.addAttribute("orphanage", orphanage);
        model.addAttribute("donations",
                donations.stream().limit(5).toList());
        model.addAttribute("recentDonations", donations);
        model.addAttribute("totalDonors", totalDonors);

        List<String> donationDates = donations.stream()
                .map(d -> d.getDonationDate().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM")))
                .toList();

        List<BigDecimal> donationAmounts = donations.stream()
                .map(Donation::getAmount)
                .toList();

        model.addAttribute("chartLabels", donationDates);
        model.addAttribute("chartAmounts", donationAmounts);

        return "orphanage-dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Orphanage orphanage = orphanageService.findByUserId(user.getId()).orElseThrow();

        model.addAttribute("orphanage", orphanage);
        return "orphanage-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String location,
            @RequestParam String description,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByUsername(authentication.getName()).orElseThrow();
            Orphanage orphanage = orphanageService.findByUserId(user.getId()).orElseThrow();

            orphanageService.updateOrphanage(
                    orphanage.getId(),
                    name,
                    location,
                    description,
                    orphanage.getContactPerson(),
                    orphanage.getContactEmail(),
                    orphanage.getContactPhone(),
                    orphanage.getRequiredFunds()
            );

            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile");
            log.error("Error updating profile: {}", e.getMessage());
        }

        return "redirect:/orphanage/profile";
    }

    @GetMapping("/donations")
    public String viewDonations(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Orphanage orphanage = orphanageService.findByUserId(user.getId()).orElseThrow();

        List<Donation> donations =
                donationService.findByOrphanageIdWithDonorUser(orphanage.getId());
        model.addAttribute("donations", donations != null ? donations : List.of());

        BigDecimal totalAmount = donations.stream()
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalAmount", totalAmount);

        long generalCount = donations.stream()
                .filter(d -> "GENERAL_DONATION".equals(d.getDonationType()))
                .count();

        long sponsorshipCount = donations.stream()
                .filter(d -> "CHILD_SPONSORSHIP".equals(d.getDonationType()))
                .count();

        model.addAttribute("generalCount", generalCount);
        model.addAttribute("sponsorshipCount", sponsorshipCount);

        return "orphanage-donations";
    }

    @GetMapping("/donors")
    public String manageDonors(Model model, Authentication authentication) {

        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Orphanage orphanage = orphanageService.findByUserId(user.getId()).orElseThrow();

        model.addAttribute("donors",
                donorService.findDonorsByOrphanage(orphanage.getId()));

        return "orphanage-donors";
    }

    @GetMapping("/reports")
    public String viewReports(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Orphanage orphanage = orphanageService.findByUserId(user.getId()).orElseThrow();

        List<Donation> donations =
                donationService.findByOrphanageIdWithDonorUser(orphanage.getId());

        long generalCount = donations.stream()
                .filter(d -> "GENERAL_DONATION".equals(d.getDonationType()))
                .count();

        long sponsorshipCount = donations.stream()
                .filter(d -> "CHILD_SPONSORSHIP".equals(d.getDonationType()))
                .count();

        List<String> lineLabels = donations.stream()
                .map(d -> d.getDonationDate().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM")))
                .toList();

        List<BigDecimal> lineAmounts = donations.stream()
                .map(Donation::getAmount)
                .toList();

        Map<String, Long> purposeCount = donations.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getPurpose() != null ? d.getPurpose() : "Other",
                        Collectors.counting()
                ));
        model.addAttribute("recentDonations", donations);
        model.addAttribute("pieLabels",
                new ArrayList<>(purposeCount.keySet()));

        model.addAttribute("pieData",
                new ArrayList<>(purposeCount.values()));
        model.addAttribute("chartLabels", lineLabels);
        model.addAttribute("chartAmounts", lineAmounts);

        return "orphanage-reports";
    }

    @GetMapping("/donors/{id}")
    public String donorDetails(@PathVariable Long id, Model model) {
        model.addAttribute("donor", donorService.findById(id));
        return "donor-details";
    }
}