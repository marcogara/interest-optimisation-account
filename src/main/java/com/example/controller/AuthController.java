package com.example.controller;

import com.example.model.BankAllocation;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class AuthController {

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private AccountService accountService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,AccountService accountService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already registered.");
            return "register";
        }

        if (result.hasErrors()) {
            return "register";
        }

        String password = user.getPassword();

        if (password.matches(PASSWORD_PATTERN)) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            model.addAttribute("success", "Account created. Please log in.");
            return "register";
        } else {
            model.addAttribute("error", "Password must contain at least 1 uppercase letter, 1 number, and be 8+ characters long");
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        System.out.println("Dashboard accessed by: " + (principal != null ? principal.getName() : "anonymous"));

        Optional<User> userOptional = userRepository.findByName(principal.getName());

        User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Dashboard: " + user.getEmail() + ", balance: " + user.getAccount());

        double interest = user.getInterest(); // e.g., 0.02
        String formattedInterest = String.format("%.2f%%", interest * 100); // → "2.00%"

        model.addAttribute("username", user.getName());
        model.addAttribute("account", user.getAccount());
        model.addAttribute("interest", formattedInterest); // pass ready string

        // test
        accountService.changePendingInterest_Test(user.getName());
        model.addAttribute("pending", user.getPendingInterestMonthlyPayment());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        model.addAttribute("today", formattedDate);
        model.addAttribute("allocations", accountService.bankAllocationList1(user.getName())); // ✅ pass allocations

        return "dashboard";
    }
}
