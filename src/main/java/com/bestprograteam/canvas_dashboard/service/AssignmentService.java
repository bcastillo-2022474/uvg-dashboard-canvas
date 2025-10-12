package com.bestprograteam.canvas_dashboard.service;

import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import com.bestprograteam.canvas_dashboard.model.entities.Course;
import com.bestprograteam.canvas_dashboard.model.entities.Submission;
import com.bestprograteam.canvas_dashboard.model.repositories.AssignmentRepository;
import com.bestprograteam.canvas_dashboard.model.repositories.CourseRepository;
import com.bestprograteam.canvas_dashboard.model.repositories.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for assignment-related business logic.
 * Handles upcoming assignments and assignment status tracking.
 */
@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
                           SubmissionRepository submissionRepository,
                           CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Get all assignments due within the next 7 days across all courses.
     * @return List of upcoming assignments
     */
    public List<Assignment> getUpcomingAssignments() {
        List<Course> courses = courseRepository.findAllActiveCourses();
        List<Assignment> allUpcoming = new ArrayList<>();

        for (Course course : courses) {
            Integer courseId = Integer.parseInt(course.getId());
            List<Assignment> courseUpcoming = assignmentRepository.findUpcomingAssignments(courseId, 7);
            allUpcoming.addAll(courseUpcoming);
        }

        return allUpcoming;
    }

    /**
     * Get upcoming assignments for a specific course.
     * @param courseId Canvas course ID
     * @param days Number of days to look ahead (default 7)
     * @return List of upcoming assignments
     */
    public List<Assignment> getUpcomingAssignmentsByCourse(Integer courseId, Integer days) {
        if (days == null) {
            days = 7;
        }
        return assignmentRepository.findUpcomingAssignments(courseId, days);
    }

    /**
     * Count assignments by status for a course.
     * Groups submissions by workflowState and counts them.
     * @param courseId Canvas course ID
     * @return Map of status -> count (e.g., "graded" -> 10, "unsubmitted" -> 2)
     */
    public Map<String, Integer> getAssignmentCountsByStatus(Integer courseId) {
        List<Submission> submissions = submissionRepository.findSubmissionsByCourseId(courseId);
        Map<String, Integer> counts = new HashMap<>();

        for (Submission submission : submissions) {
            String state = submission.getWorkflowState();
            counts.put(state, counts.getOrDefault(state, 0) + 1);
        }

        return counts;
    }

    /**
     * Get total assignment count for a course.
     * @param courseId Canvas course ID
     * @return Total number of assignments
     */
    public Integer getTotalAssignmentCount(Integer courseId) {
        List<Assignment> assignments = assignmentRepository.findAssignmentsByCourseId(courseId);
        return assignments.size();
    }

    /**
     * Get completed assignment count for a course.
     * @param courseId Canvas course ID
     * @return Number of graded assignments
     */
    public Integer getCompletedAssignmentCount(Integer courseId) {
        Map<String, Integer> counts = getAssignmentCountsByStatus(courseId);
        return counts.getOrDefault("graded", 0);
    }
}
