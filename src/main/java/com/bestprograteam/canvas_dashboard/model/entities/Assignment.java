package com.bestprograteam.canvas_dashboard.model.entities;

import java.time.LocalDateTime;

public class Assignment {
    public Integer id;
    public Integer courseId;
    public String name;
    public LocalDateTime dueAt;
    public Double pointsPossible;
    public Integer assignmentGroupId;
    public String workflowState;

    public Assignment() {}

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

    @Override
    public String toString() {
        return "Assignment{id=" + id + ", name='" + name + "', dueAt=" + dueAt + ", pointsPossible=" + pointsPossible + "}";
    }
}
