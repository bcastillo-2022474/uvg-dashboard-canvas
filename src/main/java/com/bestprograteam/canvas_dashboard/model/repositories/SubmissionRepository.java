package com.bestprograteam.canvas_dashboard.model.repositories;

import com.bestprograteam.canvas_dashboard.model.entities.Submission;
import java.util.List;

/**
 * Repository interface for Submission entity.
 * Provides access to student submission data for grade calculations and trends.
 */
public interface SubmissionRepository {

    /**
     * Get all submissions (including unsubmitted) for current student in a course.
     * @param courseId Canvas course ID
     * @return List of submissions
     */
    List<Submission> findSubmissionsByCourseId(Integer courseId);

    /**
     * Get a specific submission by assignment ID.
     * @param courseId Canvas course ID
     * @param assignmentId Canvas assignment ID
     * @return Submission or null if not found
     */
    Submission findSubmissionByAssignmentId(Integer courseId, Integer assignmentId);

    /**
     * Get the N most recent graded submissions for a course.
     * Sorted by graded_at DESC.
     * @param courseId Canvas course ID
     * @param limit Number of recent grades to return
     * @return List of recent submissions
     */
    List<Submission> findRecentGrades(Integer courseId, Integer limit);
}
