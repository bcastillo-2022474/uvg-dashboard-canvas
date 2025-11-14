package com.bestprograteam.canvas_dashboard.model.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Map;

@Repository
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CanvasCoursesRepository {

    @Value("${canvas.instance.url}")
    private String canvasInstanceUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private String getApiToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            System.out.println(details);
            if (details instanceof Map) {
                Map<String, Object> userDetails = (Map<String, Object>) details;
                return (String) userDetails.get("apiToken");
            }
        }
        return null;
    }

    public void findCurrentCourses() {
        try {
            String apiToken = getApiToken();
            System.out.println("API Token in findCurrentCourses: " + apiToken);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                canvasInstanceUrl + "/api/v1/courses?enrollment_state=active&per_page=100",
                HttpMethod.GET,
                entity,
                List.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> courses = response.getBody();
                System.out.println("=== Canvas Courses Response ===");
                System.out.println("Total courses found: " + courses.size());
                
                for (int i = 0; i < courses.size(); i++) {
                    Map<String, Object> course = courses.get(i);
                    System.out.println("\n--- Course " + (i + 1) + " ---");
                    course.forEach((key, value) -> {
                        System.out.println(key + ": " + value);
                    });
                }
                System.out.println("=== End Canvas Courses Response ===");
            } else {
                System.out.println("Failed to fetch courses. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Error fetching courses: " + e.getMessage());
            e.printStackTrace();
        }
    }
}