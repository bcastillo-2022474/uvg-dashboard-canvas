package com.bestprograteam.canvas_dashboard.model.entities;

import java.time.LocalDateTime;

public class Enrollment {
    private Integer courseId;
    private String enrollmentState;
    private Double currentScore;
    private Double finalScore;
    private String currentGrade;
    private LocalDateTime lastActivityAt;
    private Integer totalActivityTime;

    public Enrollment() {}

    public Enrollment(Integer courseId, String enrollmentState, Double currentScore, Double finalScore, String currentGrade, LocalDateTime lastActivityAt, Integer totalActivityTime) {
        this.courseId = courseId;
        this.enrollmentState = enrollmentState;
        this.currentScore = currentScore;
        this.finalScore = finalScore;
        this.currentGrade = currentGrade;
        this.lastActivityAt = lastActivityAt;
        this.totalActivityTime = totalActivityTime;
    }

    public boolean isActive() {
        return "active".equals(enrollmentState);
    }

    public boolean isCompleted() {
        return "completed".equals(enrollmentState);
    }

    public boolean isInProgress() {
        return currentScore != null && currentScore > 0;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getEnrollmentState() {
        return enrollmentState;
    }

    public void setEnrollmentState(String enrollmentState) {
        this.enrollmentState = enrollmentState;
    }

    public Double getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Double currentScore) {
        this.currentScore = currentScore;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public String getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(String currentGrade) {
        this.currentGrade = currentGrade;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public Integer getTotalActivityTime() {
        return totalActivityTime;
    }

    public void setTotalActivityTime(Integer totalActivityTime) {
        this.totalActivityTime = totalActivityTime;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "courseId=" + courseId +
                ", enrollmentState='" + enrollmentState + '\'' +
                ", currentScore=" + currentScore +
                ", finalScore=" + finalScore +
                ", currentGrade='" + currentGrade + '\'' +
                ", lastActivityAt=" + lastActivityAt +
                ", totalActivityTime=" + totalActivityTime +
                '}';
    }
}
