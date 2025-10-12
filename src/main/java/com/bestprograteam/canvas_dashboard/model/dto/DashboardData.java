package com.bestprograteam.canvas_dashboard.model.dto;

import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import java.util.List;

/**
 * DTO for complete dashboard home page data.
 * Aggregates all necessary information for the main dashboard view.
 */
public class DashboardData {

    private List<CourseCardData> courseCards;
    private List<Assignment> upcomingAssignments;
    private SemesterSummary summary;

    public DashboardData() {}

    public DashboardData(List<CourseCardData> courseCards, List<Assignment> upcomingAssignments,
                        SemesterSummary summary) {
        this.courseCards = courseCards;
        this.upcomingAssignments = upcomingAssignments;
        this.summary = summary;
    }

    public List<CourseCardData> getCourseCards() {
        return courseCards;
    }

    public void setCourseCards(List<CourseCardData> courseCards) {
        this.courseCards = courseCards;
    }

    public List<Assignment> getUpcomingAssignments() {
        return upcomingAssignments;
    }

    public void setUpcomingAssignments(List<Assignment> upcomingAssignments) {
        this.upcomingAssignments = upcomingAssignments;
    }

    public SemesterSummary getSummary() {
        return summary;
    }

    public void setSummary(SemesterSummary summary) {
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
