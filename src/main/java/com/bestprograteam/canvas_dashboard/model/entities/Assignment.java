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

    // Getters and setters + helper methods se agregarán en commit 3
}

