package com.bestprograteam.canvas_dashboard.service;

import com.bestprograteam.canvas_dashboard.model.dto.CategoryBreakdown;
import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import com.bestprograteam.canvas_dashboard.model.entities.AssignmentGroup;
import com.bestprograteam.canvas_dashboard.model.entities.Submission;
import com.bestprograteam.canvas_dashboard.model.repositories.AssignmentGroupRepository;
import com.bestprograteam.canvas_dashboard.model.repositories.AssignmentRepository;
import com.bestprograteam.canvas_dashboard.model.repositories.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for grade category calculations.
 * Handles category breakdowns and weighted grade contributions.
 */
@Service
public class CategoryService {

    private final AssignmentGroupRepository assignmentGroupRepository;
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;

    public CategoryService(AssignmentGroupRepository assignmentGroupRepository,
                          SubmissionRepository submissionRepository,
                          AssignmentRepository assignmentRepository) {
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Get grade breakdown by category for a course.
     * Calculates scores for each category (Exams 84%, Labs 89%, etc.)
     * @param courseId Canvas course ID
     * @return List of categories with computed scores
     */
    public List<CategoryBreakdown> getCategoryBreakdown(Integer courseId) {
        // 1. Get assignment groups
        List<AssignmentGroup> groups = assignmentGroupRepository.findAssignmentGroupsByCourseId(courseId);

        // 2. Get all submissions
        List<Submission> submissions = submissionRepository.findSubmissionsByCourseId(courseId);

        // 3. Get all assignments
        List<Assignment> assignments = assignmentRepository.findAssignmentsByCourseId(courseId);

        // 4. Calculate scores for each group
        List<CategoryBreakdown> breakdowns = new ArrayList<>();

        for (AssignmentGroup group : groups) {
            // Get assignments in this group
            List<Assignment> groupAssignments = assignments.stream()
                    .filter(a -> a.getAssignmentGroupId() != null && a.getAssignmentGroupId().equals(group.getId()))
                    .toList();

            int totalAssignments = groupAssignments.size();
            int pointsTotal = 0;
            int pointsEarned = 0;
            int completedAssignments = 0;

            for (Assignment assignment : groupAssignments) {
                if (assignment.getPointsPossible() != null) {
                    pointsTotal += assignment.getPointsPossible().intValue();

                    // Find submission for this assignment
                    Submission submission = submissions.stream()
                            .filter(s -> s.getAssignmentId().equals(assignment.getId()))
                            .findFirst()
                            .orElse(null);

                    if (submission != null && submission.getScore() != null) {
                        pointsEarned += submission.getScore().intValue();
                        if (submission.isGraded()) {
                            completedAssignments++;
                        }
                    }
                }
            }

            // Calculate percentage
            double currentScore = pointsTotal > 0 ? (pointsEarned * 100.0) / pointsTotal : 0.0;

            CategoryBreakdown breakdown = new CategoryBreakdown(
                    group,
                    currentScore,
                    pointsEarned,
                    pointsTotal,
                    completedAssignments,
                    totalAssignments
            );

            breakdowns.add(breakdown);
        }

        return breakdowns;
    }

    /**
     * Calculate weighted grade contribution per category.
     * Multiplies category score by group_weight for each category.
     * @param courseId Canvas course ID
     * @return Map of category name -> weighted contribution to final grade
     */
    public Map<String, Double> getCategoryContributions(Integer courseId) {
        List<CategoryBreakdown> breakdowns = getCategoryBreakdown(courseId);
        Map<String, Double> contributions = new HashMap<>();

        for (CategoryBreakdown breakdown : breakdowns) {
            AssignmentGroup group = breakdown.getGroup();
            Double categoryScore = breakdown.getCurrentScore();
            Double groupWeight = group.getGroupWeight();

            if (categoryScore != null && groupWeight != null) {
                double contribution = (categoryScore / 100.0) * groupWeight;
                contributions.put(group.getName(), contribution);
            }
        }

        return contributions;
    }
}
