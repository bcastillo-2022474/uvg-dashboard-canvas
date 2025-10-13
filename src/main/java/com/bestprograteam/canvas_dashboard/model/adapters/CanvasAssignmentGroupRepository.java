package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.AssignmentGroup;
import com.bestprograteam.canvas_dashboard.model.repositories.AssignmentGroupRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Primary
@Repository("canvasAssignmentGroupRepository")
public class CanvasAssignmentGroupRepository implements AssignmentGroupRepository {

    @Value("${canvas.instance.url}")
    private String canvasInstanceUrl;

    private final RestTemplate restTemplate;

    public CanvasAssignmentGroupRepository() {
        this.restTemplate = new RestTemplateBuilder()
                .readTimeout(Duration.ofSeconds(10))
                .connectTimeout(Duration.ofSeconds(10))
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
    public List<AssignmentGroup> findAssignmentGroupsByCourseId(Integer courseId) {
        try {
            System.out.println("[CanvasAssignmentGroupRepository] Fetching assignment groups for course " + courseId + "...");
            String apiToken = getApiToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    canvasInstanceUrl + "/api/v1/courses/" + courseId + "/assignment_groups?per_page=100",
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            System.out.println("[CanvasAssignmentGroupRepository] Successfully fetched " + (response.getBody() != null ? response.getBody().size() : 0) + " assignment groups for course " + courseId);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> groupsData = response.getBody();
                List<AssignmentGroup> groups = new ArrayList<>();

                for (Map<String, Object> data : groupsData) {
                    AssignmentGroup group = mapToAssignmentGroup(data, courseId);
                    if (group != null) {
                        groups.add(group);
                    }
                }

                return groups;
            }
        } catch (Exception e) {
            System.err.println("[CanvasAssignmentGroupRepository] ERROR fetching assignment groups for course " + courseId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public AssignmentGroup findAssignmentGroupById(Integer courseId, Integer groupId) {
        List<AssignmentGroup> groups = findAssignmentGroupsByCourseId(courseId);
        return groups.stream()
                .filter(g -> g.id.equals(groupId))
                .findFirst()
                .orElse(null);
    }

    private AssignmentGroup mapToAssignmentGroup(Map<String, Object> data, Integer courseId) {
        try {
            Integer id = (Integer) data.get("id");
            String name = (String) data.get("name");
            Integer position = (Integer) data.get("position");
            Double groupWeight = getDouble(data.get("group_weight"));

            return new AssignmentGroup(id, courseId, name, position, groupWeight);
        } catch (Exception e) {
            System.err.println("Error mapping assignment group: " + e.getMessage());
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
