package com.bestprograteam.canvas_dashboard.model.repositories;

import com.bestprograteam.canvas_dashboard.model.entities.AssignmentGroup;
import java.util.List;

/**
 * Repository interface for AssignmentGroup entity.
 * Provides access to grade category information for weighted grade calculations.
 */
public interface AssignmentGroupRepository {

    /**
     * Get all assignment groups (grade categories) for a course.
     * @param courseId Canvas course ID
     * @return List of assignment groups with weights
     */
    List<AssignmentGroup> findAssignmentGroupsByCourseId(Integer courseId);

    /**
     * Get a specific assignment group by ID.
     * @param courseId Canvas course ID
     * @param groupId Canvas assignment group ID
     * @return AssignmentGroup or null if not found
     */
    AssignmentGroup findAssignmentGroupById(Integer courseId, Integer groupId);
}
