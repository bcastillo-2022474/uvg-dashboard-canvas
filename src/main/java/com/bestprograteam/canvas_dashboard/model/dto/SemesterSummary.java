package com.bestprograteam.canvas_dashboard.model.dto;

/**
 * DTO for semester-wide summary statistics.
 * Aggregates data across all courses for top-level dashboard widgets.
 */
public class SemesterSummary {

    public Double overallPercentage;      // Average across all courses
    public Integer totalCourses;          // Number of enrolled courses
    public Integer upcomingAssignments;   // Total assignments due soon across all courses
    public String overallTrend;           // up/down/stable based on recent performance

    // Additional totals for top widgets
    public Integer totalCompletedAssignments;  // Total graded assignments across all courses
    public Integer totalGradedPoints;          // Total points earned on graded work
    public Integer totalGradedPointsPossible;  // Total possible points on graded work
    public Double totalGradedPercentage;       // Percentage for graded work only
    public Integer totalSemesterPointsPossible; // Total possible points in semester
    public Integer totalUpcomingPoints;

    public SemesterSummary() {}

    public SemesterSummary(Double overallPercentage, Integer totalCourses,
                          Integer upcomingAssignments, String overallTrend) {
        this.overallPercentage = overallPercentage;
        this.totalCourses = totalCourses;
        this.upcomingAssignments = upcomingAssignments;
        this.overallTrend = overallTrend;
    }

    @Override
    public String toString() {
        return "SemesterSummary{" +
                "overallPercentage=" + overallPercentage +
                ", totalCourses=" + totalCourses +
                ", upcomingAssignments=" + upcomingAssignments +
                ", overallTrend='" + overallTrend + '\'' +
                ", totalCompletedAssignments=" + totalCompletedAssignments +
                ", totalGradedPoints=" + totalGradedPoints +
                ", totalGradedPointsPossible=" + totalGradedPointsPossible +
                ", totalGradedPercentage=" + totalGradedPercentage +
                ", totalUpcomingPoints=" + totalUpcomingPoints +
                ", totalSemesterPointsPossible=" + totalSemesterPointsPossible +
                '}';
    }
}