package com.bestprograteam.canvas_dashboard.model.entities;

import java.time.LocalDateTime;

public class Enrollment {
    public Integer courseId;
    public String enrollmentState;
    public Double currentScore;
    public Double finalScore;
    public String currentGrade;
    public LocalDateTime lastActivityAt;
    public Integer totalActivityTime;

    public Enrollment() {}

    public Enrollment(Integer courseId, String enrollmentState, Double currentScore, Double finalScore,
                     String currentGrade, LocalDateTime lastActivityAt, Integer totalActivityTime) {
        this.courseId = courseId;
        this.enrollmentState = enrollmentState;
        this.currentScore = currentScore;
        this.finalScore = finalScore;
        this.currentGrade = currentGrade;
        this.lastActivityAt = lastActivityAt;
        this.totalActivityTime = totalActivityTime;
    }

    @Override
    public String toString() {
        return "Enrollment{courseId=" + courseId + ", currentScore=" + currentScore + ", currentGrade='" + currentGrade + "'}";
    }
}
