package com.bestprograteam.canvas_dashboard.model.entities;

/**
 * Represents a Canvas course with its core attributes.
 */
public class Course {
    private Integer id;           // Canvas course ID
    private String name;          // Full course name
    private String courseCode;    // Extracted code (e.g., "CC2008")
    private String workflowState; // Course availability status

    // Constructors
    public Course() {}

    public Course(Integer id, String name, String courseCode, String workflowState) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
        this.workflowState = workflowState;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCourseCode() {
        return courseCode;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getWorkflowState() {
        return workflowState;
    }
    public void setWorkflowState(String workflowState) {
        this.workflowState = workflowState;
    }
}

