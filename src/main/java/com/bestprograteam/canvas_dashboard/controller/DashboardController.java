package com.bestprograteam.canvas_dashboard.controller;

import com.bestprograteam.canvas_dashboard.model.repositories.CanvasCoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private CanvasCoursesRepository coursesRepository;

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
                model.addAttribute("user", userDetails);
                model.addAttribute("userName", userDetails.get("name"));
                model.addAttribute("userId", userDetails.get("id"));
                
                // Use injected repository - it will automatically have the API token from @PostConstruct
                coursesRepository.findCurrentCourses();
            }
        }
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}