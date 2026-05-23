package com.hopebridge.controller;

import com.hopebridge.entity.Donation;
import com.hopebridge.entity.Donor;
import com.hopebridge.entity.User;
import com.hopebridge.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/donor")
@Slf4j
@RequiredArgsConstructor
public class DonorController {

    private final DonorService donorService;

    private final DonationService donationService;
    private final UserService userService;
    private final OrphanageService orphanageService;


    @GetMapping("/dashboard")
    public String donorDashboard(Model model, Authentication authentication) {
        log.info("Donor dashboard accessed by: {}", authentication.getName());

        User user = userService.findByUsername(authentication.getName()).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        Donor donor = donorService.findByUserId(user.getId()).orElse(null);

        if (donor == null) {
            model.addAttribute("error", "Donor profile not found");
            return "donor-dashboard";
        }

        // Add BOTH user and donor to the model
        model.addAttribute("user", user);
        model.addAttribute("donor", donor);
        model.addAttribute("donations", donationService.findByDonorId(donor.getId()));

        return "donor-dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        Donor donor = donorService.findByUserId(user.getId()).orElse(null);

        if (donor == null) {
            model.addAttribute("error", "Donor profile not found");
            return "donor-profile";
        }

        // ✅ ADD THESE TWO LINES
        model.addAttribute("user", user);
        model.addAttribute("donor", donor);

        return "donor-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam String bio,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(authentication.getName()).orElseThrow();
            Donor donor = donorService.findByUserId(user.getId()).orElseThrow();
            
            donorService.updateDonor(donor.getId(), country, city, bio, donor.getProfileImageUrl());
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile");
            log.error("Error updating donor profile: {}", e.getMessage());
        }
        return "redirect:/donor/profile";
    }

    @GetMapping("/donate")
    public String donatePage(Model model,
                             Authentication authentication) {

        User user = userService
                .findByUsername(authentication.getName())
                .orElseThrow();

        model.addAttribute("user", user);

        model.addAttribute(
                "orphanages",
                orphanageService.findAllVerifiedOrphanages()
        );

        return "donate";
    }


    @PostMapping("/donate")
    public String generateReceipt(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String panNumber,
            @RequestParam String address,
            @RequestParam BigDecimal amount,
            @RequestParam String purpose,
            @RequestParam(required = false) String description,
            Authentication authentication,
            Model model) {

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow();

        Donor donor = donorService.findByUserId(user.getId())
                .orElseThrow();

        Long orphanageId = orphanageService.findAllVerifiedOrphanages()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No verified orphanage found"))
                .getId();

        if(amount.compareTo(BigDecimal.valueOf(1000)) > 0 &&
                (panNumber == null || panNumber.isBlank())) {

            model.addAttribute("error",
                    "PAN Number is required for donations above ₹1000");

            return "donate";
        }
        String receiptNo = "HB-" + System.currentTimeMillis();

        Donation donation = donationService.createDonation(
                donor.getId(),
                orphanageId,
                amount,
                purpose,
                description,
                panNumber,
                address,
                receiptNo
        );

//        model.addAttribute("receiptNo", receiptNo);
//        model.addAttribute("fullName", fullName);
//        model.addAttribute("email", email);
//        model.addAttribute("phone", phone);
//        model.addAttribute("panNumber", panNumber);
//        model.addAttribute("address", address);
//        model.addAttribute("amount", amount);
//        model.addAttribute("purpose", purpose);
//        model.addAttribute("description", description);
//        model.addAttribute("date", java.time.LocalDate.now());

        return "redirect:/donor/receipt/" + donation.getId();
    }

    @GetMapping("/receipt/{id}")
    public String viewReceipt(@PathVariable Long id,
                              Model model) {

        Donation donation = donationService
                .findById(id)
                .orElseThrow();

        model.addAttribute("donation", donation);

        return "donor-receipt";
    }

    @GetMapping("/donation-history")
    public String viewDonationHistory(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Donor donor = donorService.findByUserId(user.getId()).orElseThrow();

        List<Donation> donations = donationService.findByDonorId(donor.getId());
        model.addAttribute("donations", donations);
        model.addAttribute("user", user);
        model.addAttribute("donor", donor);

        return "donation-history";
    }
}
