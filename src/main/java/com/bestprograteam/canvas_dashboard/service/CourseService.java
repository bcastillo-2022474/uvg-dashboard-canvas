package com.bestprograteam.canvas_dashboard.service;

import com.bestprograteam.canvas_dashboard.model.dto.CourseWithGrades;
import com.bestprograteam.canvas_dashboard.model.entities.Course;
import com.bestprograteam.canvas_dashboard.model.entities.Enrollment;
import com.bestprograteam.canvas_dashboard.model.repositories.CourseRepository;
import com.bestprograteam.canvas_dashboard.model.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for course-related business logic.
 * Combines course information with enrollment grades for dashboard display.
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository,
                        EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Get all courses with their grades for dashboard display.
     * Combines course info with enrollment grades.
     * @return List of courses with grade information
     */
    public List<CourseWithGrades> getAllCoursesWithGrades() {
        List<Course> courses = courseRepository.findAllActiveCourses();
        List<Enrollment> enrollments = enrollmentRepository.findAllEnrollments();
        List<CourseWithGrades> result = new ArrayList<>();

        for (Course course : courses) {
            Enrollment enrollment = enrollments.stream()
                    .filter(e -> e.getCourseId().equals(Integer.parseInt(course.getId())))
                    .findFirst()
                    .orElse(null);

            result.add(new CourseWithGrades(course, enrollment));
        }

        return result;
    }

    /**
     * Get a single course with its grade information.
     * @param courseId Canvas course ID
     * @return Course with grades or null
     */
    public CourseWithGrades getCourseWithGrades(Integer courseId) {
        Course course = courseRepository.findCourseById(courseId);
        if (course == null) {
            return null;
        }

        Enrollment enrollment = enrollmentRepository.findEnrollmentByCourseId(courseId);
        return new CourseWithGrades(course, enrollment);
    }

    /**
     * Calculate overall semester percentage across all courses.
     * Averages the currentScore from all enrollments.
     * @return Weighted average percentage
     */
    public Double calculateOverallPercentage() {
        List<Enrollment> enrollments = enrollmentRepository.findAllEnrollments();

        if (enrollments.isEmpty()) {
            return 0.0;
        }

        double sum = enrollments.stream()
                .filter(e -> e.getCurrentScore() != null)
                .mapToDouble(Enrollment::getCurrentScore)
                .sum();

        long count = enrollments.stream()
                .filter(e -> e.getCurrentScore() != null)
                .count();

        return count > 0 ? sum / count : 0.0;
    }
}
