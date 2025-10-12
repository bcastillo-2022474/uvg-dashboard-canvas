package com.bestprograteam.canvas_dashboard.model.dto;

import com.bestprograteam.canvas_dashboard.model.entities.AssignmentGroup;

/**
 * DTO for grade category breakdown with computed scores.
 * Combines AssignmentGroup information with calculated performance metrics.
 * Example: "Exams 84% (42/50 points, 5/6 assignments)"
 */
public class CategoryBreakdown {

    private AssignmentGroup group;
    private Double currentScore;          // Computed percentage for this category
    private Integer pointsEarned;         // Sum of scores in this category
    private Integer pointsTotal;          // Sum of points_possible in this category
    private Integer completedAssignments; // Count of graded submissions
    private Integer totalAssignments;     // Count of all assignments in category

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

    public AssignmentGroup getGroup() {
        return group;
    }

    public void setGroup(AssignmentGroup group) {
        this.group = group;
    }

    public Double getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Double currentScore) {
        this.currentScore = currentScore;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Integer getPointsTotal() {
        return pointsTotal;
    }

    public void setPointsTotal(Integer pointsTotal) {
        this.pointsTotal = pointsTotal;
    }

    public Integer getCompletedAssignments() {
        return completedAssignments;
    }

    public void setCompletedAssignments(Integer completedAssignments) {
        this.completedAssignments = completedAssignments;
    }

    public Integer getTotalAssignments() {
        return totalAssignments;
    }

    public void setTotalAssignments(Integer totalAssignments) {
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
