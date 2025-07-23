package com.example.controller;

import com.example.model.User;
import com.example.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return "admin-dashboard"; // placeholder, can be created later
    }
}
