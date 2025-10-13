package com.bestprograteam.canvas_dashboard.model.repositories;

import com.bestprograteam.canvas_dashboard.model.entities.Enrollment;
import java.util.List;

/**
 * Repository interface for Enrollment entity.
 * Provides access to Canvas-computed grades and enrollment information.
 * This is the MOST IMPORTANT repository as it gets all course grades in one API call.
 */
public interface EnrollmentRepository {

    /**
     * Get all active enrollments for the current student.
     * Includes Canvas-computed grades (current_score, final_score).
     * @return List of enrollments with grades
     */
    List<Enrollment> findAllEnrollments();

    /**
     * Get enrollment (with grades) for a specific course.
     * @param courseId Canvas course ID
     * @return Enrollment or null if not found
     */
    Enrollment findEnrollmentByCourseId(Integer courseId);
}
