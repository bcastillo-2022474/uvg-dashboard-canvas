package com.bestprograteam.canvas_dashboard.model.entities;

import java.time.LocalDateTime;

/**
 * Assignment entity - extended version
 * Adds pointsPossible, assignmentGroupId, workflowState
 */
public class Assignment {
    private Integer id;
    private Integer courseId;
    private String name;
    private LocalDateTime dueAt;

    private Double pointsPossible;        // Points possible for semester totals
    private Integer assignmentGroupId;    // Reference to AssignmentGroup
    private String workflowState;         // Published status filter

    public Assignment() { }

    public Assignment(Integer id, Integer courseId, String name, LocalDateTime dueAt) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.dueAt = dueAt;
    }

    public Assignment(Integer id, Integer courseId, String name, LocalDateTime dueAt,
                      Double pointsPossible, Integer assignmentGroupId, String workflowState) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.dueAt = dueAt;
        this.pointsPossible = pointsPossible;
        this.assignmentGroupId = assignmentGroupId;
        this.workflowState = workflowState;
    }

// Getters y Setters
public Integer getId() {
    return id;
}

public void setId(Integer id) {
    this.id = id;
}

public Integer getCourseId() {
    return courseId;
}

public void setCourseId(Integer courseId) {
    this.courseId = courseId;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public LocalDateTime getDueAt() {
    return dueAt;
}

public void setDueAt(LocalDateTime dueAt) {
    this.dueAt = dueAt;
}

public Double getPointsPossible() {
    return pointsPossible;
}

public void setPointsPossible(Double pointsPossible) {
    this.pointsPossible = pointsPossible;
}

public Integer getAssignmentGroupId() {
    return assignmentGroupId;
}

public void setAssignmentGroupId(Integer assignmentGroupId) {
    this.assignmentGroupId = assignmentGroupId;
}

public String getWorkflowState() {
    return workflowState;
}

public void setWorkflowState(String workflowState) {
    this.workflowState = workflowState;
}

/**
 * Helper method to check if the assignment is due within 3 days
 * @return true if due soon, false otherwise
 */
public boolean isDueSoon() {
    if (dueAt == null) return false;
    LocalDateTime now = LocalDateTime.now();
    return !dueAt.isBefore(now) && dueAt.isBefore(now.plusDays(3));
}

}

