package com.bestprograteam.canvas_dashboard.model.entities;

/**
 * Represents a Canvas course for the dashboard display.
 * Includes course metadata, visual settings, and assignment counts.
 */
public class Course {
    private Integer id;                    // Canvas course ID
    private String name;                   // Full course name
    private String courseCode;             // Extracted code (e.g., "CC2008")
    private String displayName;            // Shortened UI name
    private String colorCode;              // Hex color for visual distinction
    private String workflowState;          // Course availability status
    private Integer completedAssignments;  // Count of graded submissions
    private Integer totalAssignments;      // Count of all assignments
    private Integer upcomingAssignments;   // Count due within 7 days

    /**
     * Default constructor.
     */
    public Course() {}

    /**
     * Full constructor for Course entity.
     */
    public Course(Integer id, String name, String courseCode, String displayName,
                  String colorCode, String workflowState,
                  Integer completedAssignments, Integer totalAssignments, Integer upcomingAssignments) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.workflowState = workflowState;
        this.completedAssignments = completedAssignments;
        this.totalAssignments = totalAssignments;
        this.upcomingAssignments = upcomingAssignments;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public String getWorkflowState() { return workflowState; }
    public void setWorkflowState(String workflowState) { this.workflowState = workflowState; }

    public Integer getCompletedAssignments() { return completedAssignments; }
    public void setCompletedAssignments(Integer completedAssignments) { this.completedAssignments = completedAssignments; }

    public Integer getTotalAssignments() { return totalAssignments; }
    public void setTotalAssignments(Integer totalAssignments) { this.totalAssignments = totalAssignments; }

    public Integer getUpcomingAssignments() { return upcomingAssignments; }
    public void setUpcomingAssignments(Integer upcomingAssignments) { this.upcomingAssignments = upcomingAssignments; }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", displayName='" + displayName + '\'' +
                ", colorCode='" + colorCode + '\'' +
                ", workflowState='" + workflowState + '\'' +
                ", completedAssignments=" + completedAssignments +
                ", totalAssignments=" + totalAssignments +
                ", upcomingAssignments=" + upcomingAssignments +
                '}';
    }
}

