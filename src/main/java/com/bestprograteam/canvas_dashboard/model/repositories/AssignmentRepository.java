package com.bestprograteam.canvas_dashboard.model.repositories;

import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import java.util.List;

/**
 * Repository interface for Assignment entity.
 * Provides access to assignment data for due dates and points calculations.
 */
public interface AssignmentRepository {

    /**
     * Get all assignments for a course.
     * @param courseId Canvas course ID
     * @return List of assignments
     */
    List<Assignment> findAssignmentsByCourseId(Integer courseId);

    /**
     * Get a specific assignment by ID.
     * @param courseId Canvas course ID
     * @param assignmentId Canvas assignment ID
     * @return Assignment or null if not found
     */
    Assignment findAssignmentById(Integer courseId, Integer assignmentId);

    /**
     * Get assignments due within the next N days for a course.
     * @param courseId Canvas course ID
     * @param days Number of days to look ahead
     * @return List of upcoming assignments
     */
    List<Assignment> findUpcomingAssignments(Integer courseId, Integer days);
}
