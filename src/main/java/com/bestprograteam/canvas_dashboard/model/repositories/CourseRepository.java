package com.bestprograteam.canvas_dashboard.model.repositories;

import com.bestprograteam.canvas_dashboard.model.entities.Course;
import java.util.List;

/**
 * Repository interface for Course entity.
 * Implementations will fetch course data from Canvas API or mock data sources.
 */
public interface CourseRepository {

    /**
     * Get all active courses for the current authenticated student.
     * @return List of courses with enrollment_state=active
     */
    List<Course> findAllActiveCourses();

    /**
     * Get a specific course by ID.
     * @param courseId Canvas course ID
     * @return Course or null if not found
     */
    Course findCourseById(Integer courseId);
}
