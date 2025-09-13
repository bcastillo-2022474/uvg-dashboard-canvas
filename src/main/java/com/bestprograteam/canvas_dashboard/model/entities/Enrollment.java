package com.bestprograteam.canvas_dashboard.model.entities;

public class Enrollment {
    private Integer courseId;              // Reference to Course
    private String enrollmentState;
    private Double currentScore;           // Canvas-computed grade percentage (graded only)
    private Double finalScore;             // Canvas-computed semester percentage (with zeros)
    private String currentGrade;
}
