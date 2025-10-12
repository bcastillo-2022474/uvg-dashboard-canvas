package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import com.bestprograteam.canvas_dashboard.model.repositories.AssignmentRepository;
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
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Repository("canvasAssignmentRepository")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CanvasAssignmentRepository implements AssignmentRepository {

    @Value("${canvas.instance.url}")
    private String canvasInstanceUrl;

    private final RestTemplate restTemplate;

    public CanvasAssignmentRepository() {
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
    public List<Assignment> findAssignmentsByCourseId(Integer courseId) {
        try {
            System.out.println("[CanvasAssignmentRepository] Fetching assignments for course " + courseId + "...");
            String apiToken = getApiToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    canvasInstanceUrl + "/api/v1/courses/" + courseId + "/assignments?per_page=100",
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            System.out.println("[CanvasAssignmentRepository] Successfully fetched " + (response.getBody() != null ? response.getBody().size() : 0) + " assignments for course " + courseId);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> assignmentsData = response.getBody();
                List<Assignment> assignments = new ArrayList<>();

                for (Map<String, Object> data : assignmentsData) {
                    Assignment assignment = mapToAssignment(data, courseId);
                    if (assignment != null) {
                        assignments.add(assignment);
                    }
                }

                return assignments;
            }
        } catch (Exception e) {
            System.err.println("[CanvasAssignmentRepository] ERROR fetching assignments for course " + courseId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Assignment findAssignmentById(Integer courseId, Integer assignmentId) {
        List<Assignment> assignments = findAssignmentsByCourseId(courseId);
        return assignments.stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Assignment> findUpcomingAssignments(Integer courseId, Integer days) {
        List<Assignment> assignments = findAssignmentsByCourseId(courseId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);

        return assignments.stream()
                .filter(a -> a.getDueAt() != null)
                .filter(a -> a.getDueAt().isAfter(now) && a.getDueAt().isBefore(future))
                .collect(Collectors.toList());
    }

    private Assignment mapToAssignment(Map<String, Object> data, Integer courseId) {
        try {
            Integer id = (Integer) data.get("id");
            String name = (String) data.get("name");
            String dueAtStr = (String) data.get("due_at");

            LocalDateTime dueAt = null;
            if (dueAtStr != null) {
                try {
                    dueAt = LocalDateTime.parse(dueAtStr.substring(0, 19));
                } catch (Exception e) {
                }
            }

            Double pointsPossible = getDouble(data.get("points_possible"));
            Integer assignmentGroupId = (Integer) data.get("assignment_group_id");
            String workflowState = (String) data.get("workflow_state");

            return new Assignment(id, courseId, name, dueAt, pointsPossible,
                    assignmentGroupId, workflowState);
        } catch (Exception e) {
            System.err.println("Error mapping assignment: " + e.getMessage());
            return null;
        }
    }

    private Double getDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        return null;
    }
}
