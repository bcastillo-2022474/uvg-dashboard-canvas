package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.Course;
import com.bestprograteam.canvas_dashboard.model.repositories.CourseRepository;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Primary
@Repository("canvasCourseRepository")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CanvasCourseRepository implements CourseRepository {

    @Value("${canvas.instance.url}")
    private String canvasInstanceUrl;

    private final RestTemplate restTemplate;

    public CanvasCourseRepository() {
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
    public List<Course> findAllActiveCourses() {
        try {
            System.out.println("[CanvasCourseRepository] Fetching courses from Canvas API...");
            String apiToken = getApiToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    canvasInstanceUrl + "/api/v1/courses?enrollment_state=active&per_page=100",
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            System.out.println("[CanvasCourseRepository] Successfully fetched " + (response.getBody() != null ? response.getBody().size() : 0) + " courses");

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> coursesData = response.getBody();
                List<Course> courses = new ArrayList<>();

                for (Map<String, Object> data : coursesData) {
                    Course course = mapToCourse(data);
                    if (course != null) {
                        courses.add(course);
                    }
                }

                return courses;
            }
        } catch (Exception e) {
            System.err.println("[CanvasCourseRepository] ERROR fetching courses: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Course findCourseById(Integer courseId) {
        List<Course> courses = findAllActiveCourses();
        return courses.stream()
                .filter(c -> c.getId().equals(String.valueOf(courseId)))
                .findFirst()
                .orElse(null);
    }

    private Course mapToCourse(Map<String, Object> data) {
        try {
            String id = String.valueOf(data.get("id"));
            String name = (String) data.get("name");
            String courseCode = (String) data.get("course_code");
            String workflowState = (String) data.get("workflow_state");

            String enrollmentType = "StudentEnrollment";
            Date startDate = new Date(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000);
            Date endDate = new Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000);
            float totalPoints = 0f;
            float currentGrade = 0f;

            return new Course(id, name, courseCode, workflowState, enrollmentType,
                    startDate, endDate, totalPoints, currentGrade);
        } catch (Exception e) {
            System.err.println("Error mapping course: " + e.getMessage());
            return null;
        }
    }
}
