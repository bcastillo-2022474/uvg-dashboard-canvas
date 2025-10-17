package com.bestprograteam.canvas_dashboard.model.dto;

import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import java.util.List;

/**
 * DTO for complete dashboard home page data.
 * Aggregates all necessary information for the main dashboard view.
 */
public class DashboardData {

    public List<CourseCardData> courseCards;
    public List<Assignment> upcomingAssignments;
    public SemesterSummary summary;

    public DashboardData() {}

    public DashboardData(List<CourseCardData> courseCards, List<Assignment> upcomingAssignments,
                        SemesterSummary summary) {
        this.courseCards = courseCards;
        this.upcomingAssignments = upcomingAssignments;
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "DashboardData{" +
                "courseCards=" + courseCards +
                ", upcomingAssignments=" + upcomingAssignments +
                ", summary=" + summary +
                '}';
    }
}
