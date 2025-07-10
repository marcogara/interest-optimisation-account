package com.example.authcore.controller;

import com.example.authcore.model.User;
import com.example.authcore.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Maps to login.html
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        /**
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already registered.");
            return "register";
        }
         */
        if (result.hasErrors()) {
            return "register";
        }

        String password = user.getPassword();
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

        if (password.matches(pattern)) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            model.addAttribute("success", "Account created. Please log in.");
            return "register";
        } else {
            model.addAttribute("error", "Password must contain at least 1 uppercase letter, 1 number, and be 8+ characters long");
            return "register";
        }
    }
}
