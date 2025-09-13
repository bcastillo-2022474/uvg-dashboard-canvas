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

    public List<GradingPeriod> getGradingPeriods() {
       
        return null;
    }

    public float calculateProgress() {
        return totalPoints > 0 ? (currentGrade / totalPoints) * 100 : 0;
    }

    public Assignment getNextAssignment() {
        return null;
    }

    public float getTotalPointsEarned() {
        return currentGrade;
    }
}

