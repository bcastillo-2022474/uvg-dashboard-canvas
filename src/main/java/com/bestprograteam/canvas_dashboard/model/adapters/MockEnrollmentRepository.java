package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.Enrollment;
import com.bestprograteam.canvas_dashboard.model.repositories.EnrollmentRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of EnrollmentRepository for testing.
 * Returns hardcoded enrollment data with Canvas-computed grades.
 */
@Repository
public class MockEnrollmentRepository implements EnrollmentRepository {

    private final List<Enrollment> mockEnrollments;

    public MockEnrollmentRepository() {
        mockEnrollments = new ArrayList<>();

        // Enrollment for course 101 - Data Structures (85%)
        Enrollment enrollment1 = new Enrollment(
                101,
                "active",
                85.0,
                82.5,
                "B",
                LocalDateTime.now().minusDays(2),
                14400
        );
        mockEnrollments.add(enrollment1);

        // Enrollment for course 102 - OOP (85%)
        Enrollment enrollment2 = new Enrollment(
                102,
                "active",
                85.0,
                83.0,
                "B",
                LocalDateTime.now().minusDays(1),
                18000
        );
        mockEnrollments.add(enrollment2);

        // Enrollment for course 103 - Database (84%)
        Enrollment enrollment3 = new Enrollment(
                103,
                "active",
                84.0,
                80.0,
                "B",
                LocalDateTime.now().minusDays(3),
                12600
        );
        mockEnrollments.add(enrollment3);

        // Enrollment for course 104 - Software Engineering (90%)
        Enrollment enrollment4 = new Enrollment(
                104,
                "active",
                90.0,
                88.5,
                "A",
                LocalDateTime.now().minusDays(1),
                16200
        );
        mockEnrollments.add(enrollment4);
    }

    @Override
    public List<Enrollment> findAllEnrollments() {
        return new ArrayList<>(mockEnrollments);
    }

    @Override
    public Enrollment findEnrollmentByCourseId(Integer courseId) {
        return mockEnrollments.stream()
                .filter(e -> e.courseId.equals(courseId))
                .findFirst()
                .orElse(null);
    }
}
