package com.bestprograteam.canvas_dashboard.model.dto;

/**
 * DTO for semester-wide summary statistics.
 * Aggregates data across all courses for top-level dashboard widgets.
 */
public class SemesterSummary {

    private Double overallPercentage;      // Average across all courses
    private Integer totalCourses;          // Number of enrolled courses
    private Integer upcomingAssignments;   // Total assignments due soon across all courses
    private String overallTrend;           // up/down/stable based on recent performance

    // Additional totals for top widgets
    private Integer totalCompletedAssignments;  // Total graded assignments across all courses
    private Integer totalGradedPoints;          // Total points earned on graded work
    private Integer totalGradedPointsPossible;  // Total possible points on graded work
    private Double totalGradedPercentage;       // Percentage for graded work only
    private Integer totalSemesterPoints;        // Total points earned including zeros
    private Integer totalSemesterPointsPossible; // Total possible points in semester

    public SemesterSummary() {}

    public SemesterSummary(Double overallPercentage, Integer totalCourses,
                          Integer upcomingAssignments, String overallTrend) {
        this.overallPercentage = overallPercentage;
        this.totalCourses = totalCourses;
        this.upcomingAssignments = upcomingAssignments;
        this.overallTrend = overallTrend;
    }

    public Double getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(Double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public Integer getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Integer totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Integer getUpcomingAssignments() {
        return upcomingAssignments;
    }

    public void setUpcomingAssignments(Integer upcomingAssignments) {
        this.upcomingAssignments = upcomingAssignments;
    }

    public String getOverallTrend() {
        return overallTrend;
    }

    public void setOverallTrend(String overallTrend) {
        this.overallTrend = overallTrend;
    }

    public Integer getTotalCompletedAssignments() {
        return totalCompletedAssignments;
    }

    public void setTotalCompletedAssignments(Integer totalCompletedAssignments) {
        this.totalCompletedAssignments = totalCompletedAssignments;
    }

    public Integer getTotalGradedPoints() {
        return totalGradedPoints;
    }

    public void setTotalGradedPoints(Integer totalGradedPoints) {
        this.totalGradedPoints = totalGradedPoints;
    }

    public Integer getTotalGradedPointsPossible() {
        return totalGradedPointsPossible;
    }

    public void setTotalGradedPointsPossible(Integer totalGradedPointsPossible) {
        this.totalGradedPointsPossible = totalGradedPointsPossible;
    }

    public Double getTotalGradedPercentage() {
        return totalGradedPercentage;
    }

    public void setTotalGradedPercentage(Double totalGradedPercentage) {
        this.totalGradedPercentage = totalGradedPercentage;
    }

    public Integer getTotalSemesterPoints() {
        return totalSemesterPoints;
    }

    public void setTotalSemesterPoints(Integer totalSemesterPoints) {
        this.totalSemesterPoints = totalSemesterPoints;
    }

    public Integer getTotalSemesterPointsPossible() {
        return totalSemesterPointsPossible;
    }

    public void setTotalSemesterPointsPossible(Integer totalSemesterPointsPossible) {
        this.totalSemesterPointsPossible = totalSemesterPointsPossible;
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
                ", totalSemesterPoints=" + totalSemesterPoints +
                ", totalSemesterPointsPossible=" + totalSemesterPointsPossible +
                '}';
    }
}
