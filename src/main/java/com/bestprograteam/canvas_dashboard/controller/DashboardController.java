package com.bestprograteam.canvas_dashboard.controller;

import com.bestprograteam.canvas_dashboard.model.dto.DashboardData;
import com.bestprograteam.canvas_dashboard.model.dto.PredictionData;
import com.bestprograteam.canvas_dashboard.model.services.DashboardService;
import com.bestprograteam.canvas_dashboard.model.services.PredictionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final PredictionService predictionService;

    public DashboardController(DashboardService dashboardService, PredictionService predictionService) {
        this.dashboardService = dashboardService;
        this.predictionService = predictionService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();

            if (details instanceof Map) {
                Map<String, Object> userDetails = (Map<String, Object>) details;
                String userName = (String) userDetails.get("name");

                model.addAttribute("user", userDetails);
                model.addAttribute("userName", userName);
                model.addAttribute("userId", userDetails.get("id"));
                model.addAttribute("userInitials", extractInitials(userName));

                DashboardData dashboardData = dashboardService.getDashboardData();
                model.addAttribute("dashboardData", dashboardData);

                PredictionData predictionData = predictionService.calculatePredictions(dashboardData);
                model.addAttribute("predictionData", predictionData);
            }
        }
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Extract initials from a full name.
     * Examples: "John Doe" -> "JD", "Alice" -> "A", "Bob Smith Jr" -> "BS"
     */
    private String extractInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "?";
        }

        String[] parts = fullName.trim().split("\\s+");

        if (parts.length == 0) {
            return "?";
        }

        StringBuilder initials = new StringBuilder();

        // First initial
        if (parts[0].length() > 0) {
            initials.append(parts[0].charAt(0));
        }

        // Second initial (if exists)
        if (parts.length > 1 && parts[1].length() > 0) {
            initials.append(parts[1].charAt(0));
        }

        return initials.toString().toUpperCase();
    }
}