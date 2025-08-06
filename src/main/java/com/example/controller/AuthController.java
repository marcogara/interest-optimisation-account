package com.example.controller;

import com.example.model.BankAllocation;
import com.example.model.User;
import com.example.service.AccountService;
import com.example.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private final AccountService accountService;

    public AuthController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login"; // Maps to login.html
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already registered.");
            return "register";
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.registerUser(user);
        model.addAttribute("success", "Account created. Please log in.");
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        System.out.println("Dashboard accessed by: " + (principal != null ? principal.getName() : "anonymous"));

        Optional<User> userOptional = userService.findByName(principal.getName());

        User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Dashboard: " + user.getEmail() + ", balance: " + user.getAccount());

        double interest = user.getInterest(); // e.g., 0.02
        String formattedInterest = String.format("%.2f%%", interest * 100); // → "2.00%"

        model.addAttribute("username", user.getName());
        model.addAttribute("account", user.getAccount());
        model.addAttribute("interest", formattedInterest); // pass ready string
        model.addAttribute("pending", user.getPendingInterestMonthlyPayment());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        model.addAttribute("today", formattedDate);

        List<BankAllocation> allocations = accountService.bankAllocationList1(user.getName());
        if (!allocations.isEmpty()) {
            model.addAttribute("allocations", allocations);
        }

        return "dashboard";
    }
}
