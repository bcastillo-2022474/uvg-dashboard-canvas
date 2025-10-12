package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.Course;
import com.bestprograteam.canvas_dashboard.model.repositories.CourseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Mock implementation of CourseRepository for testing.
 * Returns hardcoded test data without hitting Canvas API.
 */
@Repository
public class MockCourseRepository implements CourseRepository {

    private final List<Course> mockCourses;

    public MockCourseRepository() {
        mockCourses = new ArrayList<>();

        // Mock course 1 - Data Structures
        Course course1 = new Course(
                "101",
                "Data Structures and Algorithms",
                "CS2021",
                "active",
                "StudentEnrollment",
                new Date(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000), // 90 days ago
                new Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000), // 90 days from now
                500.0f,
                425.0f
        );
        mockCourses.add(course1);

        // Mock course 2 - Object-Oriented Programming
        Course course2 = new Course(
                "102",
                "Object-Oriented Programming",
                "CC2008",
                "active",
                "StudentEnrollment",
                new Date(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000),
                new Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000),
                600.0f,
                510.0f
        );
        mockCourses.add(course2);

        // Mock course 3 - Database Systems
        Course course3 = new Course(
                "103",
                "Database Systems",
                "CS3042",
                "active",
                "StudentEnrollment",
                new Date(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000),
                new Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000),
                450.0f,
                378.0f
        );
        mockCourses.add(course3);

        // Mock course 4 - Software Engineering
        Course course4 = new Course(
                "104",
                "Software Engineering",
                "CS3071",
                "active",
                "StudentEnrollment",
                new Date(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000),
                new Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000),
                550.0f,
                495.0f
        );
        mockCourses.add(course4);
    }

    @Override
    public List<Course> findAllActiveCourses() {
        return new ArrayList<>(mockCourses);
    }

    @Override
    public Course findCourseById(Integer courseId) {
        return mockCourses.stream()
                .filter(c -> c.getId().equals(String.valueOf(courseId)))
                .findFirst()
                .orElse(null);
    }
}
