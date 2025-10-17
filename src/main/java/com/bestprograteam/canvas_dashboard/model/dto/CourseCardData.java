package com.bestprograteam.canvas_dashboard.model.dto;

import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import java.util.List;

/**
 * DTO for complete course card data.
 * Contains all information needed to display a single course card.
 */
public class CourseCardData {

    public CourseWithGrades courseWithGrades;
    public List<RecentGrade> recentGrades;
    public List<CategoryBreakdown> categoryBreakdown;
    public List<Assignment> upcomingAssignments;
    public String trend;
    public double remainingPoints;

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