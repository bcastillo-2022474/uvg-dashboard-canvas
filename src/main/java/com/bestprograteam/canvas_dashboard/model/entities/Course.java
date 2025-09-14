package com.bestprograteam.canvas_dashboard.model.entities;

/**
 * Represents a Canvas course with core and display attributes.
 */
public class Course {
    private Integer id;                    // Canvas course ID
    private String name;                   // Full course name
    private String courseCode;             // Extracted code (e.g., "CC2008")
    private String workflowState;          // Course availability status

    // New display fields
    private String displayName;            // Shortened UI name
    private String colorCode;              // Hex color for visual distinction

    // Assignment counters
    private Integer completedAssignments; // Count of graded submissions
    private Integer totalAssignments;     // Count of all assignments
    private Integer upcomingAssignments;  // Count due within 7 days

    // Constructors
    public Course() {}

    public Course(Integer id, String name, String courseCode, String workflowState,
                  String displayName, String colorCode,
                  Integer completedAssignments, Integer totalAssignments, Integer upcomingAssignments) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
        this.workflowState = workflowState;
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.completedAssignments = completedAssignments;
        this.totalAssignments = totalAssignments;
        this.upcomingAssignments = upcomingAssignments;
    }

    // Getters & Setters (core fields)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getWorkflowState() { return workflowState; }
    public void setWorkflowState(String workflowState) { this.workflowState = workflowState; }

    // Getters & Setters (display + counters)
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public Integer getCompletedAssignments() { return completedAssignments; }
    public void setCompletedAssignments(Integer completedAssignments) { this.completedAssignments = completedAssignments; }

    public Integer getTotalAssignments() { return totalAssignments; }
    public void setTotalAssignments(Integer totalAssignments) { this.totalAssignments = totalAssignments; }

    public Integer getUpcomingAssignments() { return upcomingAssignments; }
    public void setUpcomingAssignments(Integer upcomingAssignments) { this.upcomingAssignments = upcomingAssignments; }
}

