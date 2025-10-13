package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.Submission;
import com.bestprograteam.canvas_dashboard.model.repositories.SubmissionRepository;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Repository("canvasSubmissionRepository")
public class CanvasSubmissionRepository implements SubmissionRepository {

    @Value("${canvas.instance.url}")
    private String canvasInstanceUrl;

    private final RestTemplate restTemplate;

    public CanvasSubmissionRepository() {
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
    public List<Submission> findSubmissionsByCourseId(Integer courseId) {
        try {
            System.out.println("[CanvasSubmissionRepository] Fetching submissions for course " + courseId + "...");
            String apiToken = getApiToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    canvasInstanceUrl + "/api/v1/courses/" + courseId + "/students/submissions?student_id=self&per_page=100",
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            System.out.println("[CanvasSubmissionRepository] Successfully fetched " + (response.getBody() != null ? response.getBody().size() : 0) + " submissions for course " + courseId);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> submissionsData = response.getBody();
                List<Submission> submissions = new ArrayList<>();

                for (Map<String, Object> data : submissionsData) {
                    Submission submission = mapToSubmission(data);
                    if (submission != null) {
                        submissions.add(submission);
                    }
                }

                return submissions;
            }
        } catch (Exception e) {
            System.err.println("[CanvasSubmissionRepository] ERROR fetching submissions for course " + courseId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Submission findSubmissionByAssignmentId(Integer courseId, Integer assignmentId) {
        List<Submission> submissions = findSubmissionsByCourseId(courseId);
        return submissions.stream()
                .filter(s -> s.assignmentId.equals(assignmentId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Submission> findRecentGrades(Integer courseId, Integer limit) {
        List<Submission> submissions = findSubmissionsByCourseId(courseId);
        return submissions.stream()
                .filter(s -> s.gradedAt != null)
                .sorted(Comparator.comparing((Submission s) -> s.gradedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Submission mapToSubmission(Map<String, Object> data) {
        try {
            Integer assignmentId = (Integer) data.get("assignment_id");
            Double score = getDouble(data.get("score"));
            String workflowState = (String) data.get("workflow_state");

            String gradedAtStr = (String) data.get("graded_at");
            LocalDateTime gradedAt = null;
            if (gradedAtStr != null) {
                try {
                    gradedAt = LocalDateTime.parse(gradedAtStr.substring(0, 19));
                } catch (Exception e) {
                }
            }

            Boolean late = (Boolean) data.get("late");

            return new Submission(assignmentId, score, workflowState, gradedAt, late);
        } catch (Exception e) {
            System.err.println("Error mapping submission: " + e.getMessage());
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
