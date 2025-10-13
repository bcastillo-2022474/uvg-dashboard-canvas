package com.bestprograteam.canvas_dashboard.model.dto;

import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import java.util.List;

/**
 * DTO for complete course card data.
 * Contains all information needed to display a single course card.
 */
public class CourseCardData {

    private CourseWithGrades courseWithGrades;
    private List<RecentGrade> recentGrades;
    private List<CategoryBreakdown> categoryBreakdown;
    private List<Assignment> upcomingAssignments;
    private String trend;
    private double remainingPoints;

    public CourseCardData() {}

    public CourseCardData(CourseWithGrades courseWithGrades, List<RecentGrade> recentGrades,
                         List<CategoryBreakdown> categoryBreakdown, List<Assignment> upcomingAssignments,
                         String trend, double remainingPoints) {
        this.courseWithGrades = courseWithGrades;
        this.recentGrades = recentGrades;
        this.categoryBreakdown = categoryBreakdown;
        this.upcomingAssignments = upcomingAssignments;
        this.trend = trend;
        this.remainingPoints = remainingPoints;
    }

    public CourseWithGrades getCourseWithGrades() {
        return courseWithGrades;
    }

    public void setCourseWithGrades(CourseWithGrades courseWithGrades) {
        this.courseWithGrades = courseWithGrades;
    }

    public List<RecentGrade> getRecentGrades() {
        return recentGrades;
    }

    public void setRecentGrades(List<RecentGrade> recentGrades) {
        this.recentGrades = recentGrades;
    }

    public List<CategoryBreakdown> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(List<CategoryBreakdown> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public List<Assignment> getUpcomingAssignments() {
        return upcomingAssignments;
    }

    public void setUpcomingAssignments(List<Assignment> upcomingAssignments) {
        this.upcomingAssignments = upcomingAssignments;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public double getRemainingPoints() {
        return remainingPoints;
    }

    public void setRemainingPoints(double remainingPoints) {
        this.remainingPoints = remainingPoints;
    }

    @Override
    public String toString() {
        return "CourseCardData{" +
                "courseWithGrades=" + courseWithGrades +
                ", recentGrades=" + recentGrades +
                ", categoryBreakdown=" + categoryBreakdown +
                ", upcomingAssignments=" + upcomingAssignments +
                ", trend='" + trend + "'" +
                ", remainingPoints=" + remainingPoints +
                '}';
    }
}