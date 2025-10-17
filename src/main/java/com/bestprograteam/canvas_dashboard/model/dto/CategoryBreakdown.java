package com.bestprograteam.canvas_dashboard.model.dto;

import com.bestprograteam.canvas_dashboard.model.entities.AssignmentGroup;

/**
 * DTO for grade category breakdown with computed scores.
 * Combines AssignmentGroup information with calculated performance metrics.
 * Example: "Exams 84% (42/50 points, 5/6 assignments)"
 */
public class CategoryBreakdown {

    public AssignmentGroup group;
    public Double currentScore;          // Computed percentage for this category
    public Integer pointsEarned;         // Sum of scores in this category
    public Integer pointsTotal;          // Sum of points_possible in this category
    public Integer completedAssignments; // Count of graded submissions
    public Integer totalAssignments;     // Count of all assignments in category

    public CategoryBreakdown() {}

    public CategoryBreakdown(AssignmentGroup group, Double currentScore, Integer pointsEarned,
                           Integer pointsTotal, Integer completedAssignments, Integer totalAssignments) {
        this.group = group;
        this.currentScore = currentScore;
        this.pointsEarned = pointsEarned;
        this.pointsTotal = pointsTotal;
        this.completedAssignments = completedAssignments;
        this.totalAssignments = totalAssignments;
    }

    @Override
    public String toString() {
        return "CategoryBreakdown{" +
                "group=" + group +
                ", currentScore=" + currentScore +
                ", pointsEarned=" + pointsEarned +
                ", pointsTotal=" + pointsTotal +
                ", completedAssignments=" + completedAssignments +
                ", totalAssignments=" + totalAssignments +
                '}';
    }
}
