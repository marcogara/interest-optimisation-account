package com.example.controller;

import com.example.model.Bank;
import com.example.model.User;
import com.example.service.BankAllocationService;
import com.example.service.BankService;
import com.example.service.InterestSnapshotService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    private final UserService userService;
    private final BankService bankService;
    private final BankAllocationService bankAllocationService;
    private final InterestSnapshotService interestSnapshotService;

    public AdminController(UserService userService, BankService bankService, BankAllocationService bankAllocationService, InterestSnapshotService interestSnapshotService) {
        this.userService = userService;
        this.bankService = bankService;
        this.bankAllocationService = bankAllocationService;
        this.interestSnapshotService = interestSnapshotService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Principal principal, HttpServletResponse response) {
        if (principal == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        User user = userService.findByName(principal.getName())
                .orElse(null);

        // Prepare formatted bank data
        List<Bank> banks = bankService.findAll();
        List<Map<String, String>> formattedBanks = new ArrayList<>();

        for (Bank bank : banks) {
            Map<String, String> entry = new HashMap<>();
            entry.put("name", bank.getName());
            entry.put("interest", String.format("%.2f%%", bank.getInterest() * 100));
            entry.put("account", String.format("%.2f", bank.getAccount()));
            formattedBanks.add(entry);
        }

        // Prepare Users
        List<User> users = userService.findAll();
        List<Map<String, String>> formattedUsers = new ArrayList<>();

        for (User user1 : users) {
            if (!"ADMIN".equals(user1.getRole())) {
                Map<String, String> entry = new HashMap<>();
                entry.put("name", user1.getName());
                entry.put("interest", String.format("%.2f%%", user1.getInterest() * 100));
                entry.put("account", String.format("%.2f", user1.getAccount()));
                formattedUsers.add(entry);
            }
        }

        List<Map<String, String>> formattedProjections = new ArrayList<>();

        for (User user1 : users) {
            if (!"ADMIN".equals(user1.getRole())) {
                Map<String, String> entry = new HashMap<>();
                entry.put("name", user1.getName());
                double projection = user1.getAccount() * user1.getInterest() + user1.getAccount();
                entry.put("projection", String.format("%.2f", projection));
                formattedProjections.add(entry);
            }
        }

        model.addAttribute("projections", formattedProjections);
        model.addAttribute("username", user.getName());
        model.addAttribute("banks", formattedBanks);
        model.addAttribute("users", formattedUsers);
        model.addAttribute("allocations", bankAllocationService.findAll()); // ✅ pass allocations

        return "admin-dashboard"; // placeholder, can be created later
    }

    @PostMapping("/admin/bank/register")
    public String registerBank(@RequestParam String name,
                               @RequestParam double interest,
                               @RequestParam double account,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            Bank bank = new Bank(name, interest, account);
            bankService.save(bank);

            redirectAttributes.addFlashAttribute("success", "✅ Bank created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Failed to create bank: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/snapshot")
    public String captureInterestSnapshot(Model model, RedirectAttributes redirectAttributes) {
        try {
            interestSnapshotService.snapshotCurrentInterest();
            redirectAttributes.addFlashAttribute("snapshotSuccess", "✅ Interest snapshot captured.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("snapshotError", "❌ Failed to capture snapshot: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}