package com.example.controller;

import com.example.model.Bank;
import com.example.model.User;
import com.example.repository.BankRepository;
import com.example.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final BankRepository bankRepository;

    public AdminController(UserRepository userRepository, BankRepository bankRepository) {
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Principal principal, HttpServletResponse response) {
        if (principal == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        User user = userRepository.findByName(principal.getName())
                .orElse(null);

        // Only allow access if username is "admin"
        if (user == null || !"admin".equals(user.getName())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "redirect:/dashboard"; // or a custom 403 page
        }

        model.addAttribute("username", user.getName());
        model.addAttribute("banks", bankRepository.findAll()); // ✅ Pass list of banks
        return "admin-dashboard"; // placeholder, can be created later
    }

    @PostMapping("/admin/bank/register")
    public String registerBank(@RequestParam String name,
                               @RequestParam double interest,
                               @RequestParam double account,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (!"admin".equals(principal.getName())) {
            return "redirect:/dashboard";
        }

        try {
            Bank bank = new Bank(name, interest, account);
            bankRepository.save(bank);

            redirectAttributes.addFlashAttribute("success", "✅ Bank created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Failed to create bank: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

}
