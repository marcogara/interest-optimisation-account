package com.example.simulation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class InterestSimulationController {

    private final InterestSimulationService simulationService;

    public InterestSimulationController(InterestSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/admin/simulate-interest")
    public String simulateInterestChanges(Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null || !"admin".equals(principal.getName())) {
            return "redirect:/dashboard";
        }

        simulationService.simulateInterestChanges();
        redirectAttributes.addFlashAttribute("success", "Interest rates simulated successfully.");
        return "redirect:/admin/dashboard";
    }
}
