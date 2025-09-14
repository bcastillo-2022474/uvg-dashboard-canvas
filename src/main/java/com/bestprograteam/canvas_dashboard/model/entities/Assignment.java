package com.bestprograteam.canvas_dashboard.model.entities;

import java.time.LocalDateTime;

/**
 * Assignment entity - basic version
 * Fields: id, courseId, name, dueAt
 */
public class Assignment {
    private Integer id;
    private Integer courseId;
    private String name;
    private LocalDateTime dueAt;

    public Assignment() { }

    public Assignment(Integer id, Integer courseId, String name, LocalDateTime dueAt) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.dueAt = dueAt;
    }

    // Lo demás (description, submissionTypes, etc.) se agregará en commits posteriores
}

