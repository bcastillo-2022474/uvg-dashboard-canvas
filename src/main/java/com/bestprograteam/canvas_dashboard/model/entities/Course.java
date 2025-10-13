package com.bestprograteam.canvas_dashboard.model.entities;

import java.util.Date;
import java.util.List;

public class Course {
    private String id;
    private String name;
    private String code;
    private String status;
    private String enrollmentType;
    private Date startDate;
    private Date endDate;
    private float totalPoints;
    private float currentGrade;

    public Course(String id, String name, String code, String status, String enrollmentType, Date startDate, Date endDate, float totalPoints, float currentGrade) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.status = status;
        this.enrollmentType = enrollmentType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPoints = totalPoints;
        this.currentGrade = currentGrade;
    }

    public boolean isActive() {
        Date today = new Date();
        return (today.after(startDate) && today.before(endDate));
    }

//    public List<GradingPeriod> getGradingPeriods() {
//
//        return null;
//    }

    public float calculateProgress() {
        return totalPoints > 0 ? (currentGrade / totalPoints) * 100 : 0;
    }

    public Assignment getNextAssignment() {
        return null;
    }

    public float getTotalPointsEarned() {
        return currentGrade;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public float getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(float totalPoints) {
        this.totalPoints = totalPoints;
    }

    public float getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(float currentGrade) {
        this.currentGrade = currentGrade;
    }
}

