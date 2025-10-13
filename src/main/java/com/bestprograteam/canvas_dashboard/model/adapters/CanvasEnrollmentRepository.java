package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.Enrollment;
import com.bestprograteam.canvas_dashboard.model.repositories.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Primary
@Repository("canvasEnrollmentRepository")
public class CanvasEnrollmentRepository implements EnrollmentRepository {

    @Value("${canvas.instance.url}")
    private String canvasInstanceUrl;

    private final RestTemplate restTemplate;

    public CanvasEnrollmentRepository() {
        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    private String getApiToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            if (details instanceof Map) {
                Map<String, Object> userDetails = (Map<String, Object>) details;
                return (String) userDetails.get("apiToken");
            }
        }
        return null;
    }

    @Override
    public List<Enrollment> findAllEnrollments() {
        try {
            System.out.println("[CanvasEnrollmentRepository] Fetching enrollments from Canvas API...");
            String apiToken = getApiToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    canvasInstanceUrl + "/api/v1/users/self/enrollments?per_page=100",
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            System.out.println("[CanvasEnrollmentRepository] Successfully fetched " + (response.getBody() != null ? response.getBody().size() : 0) + " enrollments");

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> enrollmentsData = response.getBody();
                List<Enrollment> enrollments = new ArrayList<>();

                for (Map<String, Object> data : enrollmentsData) {
                    Enrollment enrollment = mapToEnrollment(data);
                    if (enrollment != null) {
                        enrollments.add(enrollment);
                    }
                }

                return enrollments;
            }
        } catch (Exception e) {
            System.err.println("[CanvasEnrollmentRepository] ERROR fetching enrollments: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Enrollment findEnrollmentByCourseId(Integer courseId) {
        List<Enrollment> enrollments = findAllEnrollments();
        return enrollments.stream()
                .filter(e -> e.getCourseId().equals(courseId))
                .findFirst()
                .orElse(null);
    }

    private Enrollment mapToEnrollment(Map<String, Object> data) {
        try {
            Map<String, Object> grades = (Map<String, Object>) data.get("grades");

            Integer courseId = (Integer) data.get("course_id");
            String enrollmentState = (String) data.get("enrollment_state");

            Double currentScore = grades != null ? getDouble(grades.get("current_score")) : null;
            Double finalScore = grades != null ? getDouble(grades.get("final_score")) : null;
            String currentGrade = grades != null ? (String) grades.get("current_grade") : null;

            String lastActivityAtStr = (String) data.get("last_activity_at");
            LocalDateTime lastActivityAt = lastActivityAtStr != null
                    ? LocalDateTime.parse(lastActivityAtStr.substring(0, 19))
                    : null;

            Integer totalActivityTime = (Integer) data.get("total_activity_time");

            return new Enrollment(courseId, enrollmentState, currentScore, finalScore,
                    currentGrade, lastActivityAt, totalActivityTime);
        } catch (Exception e) {
            System.err.println("Error mapping enrollment: " + e.getMessage());
            return null;
        }
    }

    private Double getDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
