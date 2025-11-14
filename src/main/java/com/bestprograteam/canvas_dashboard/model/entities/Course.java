package com.bestprograteam.canvas_dashboard.model.entities;

import java.util.Date;

public class Course {
    public String id;
    public String name;
    public String code;
    public String status;
    public String enrollmentType;
    public Date startDate;
    public Date endDate;
    public float totalPoints;
    public float currentGrade;
    public String courseColor;
    

    public Course() {}

    public Course(String id, String name, String code, String status, String enrollmentType,
                  Date startDate, Date endDate, float totalPoints, float currentGrade, String courseColor) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.status = status;
        this.enrollmentType = enrollmentType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPoints = totalPoints;
        this.currentGrade = currentGrade;
        this.courseColor = courseColor;
    }

    @Override
    public String toString() {
        return "Course{id='" + id + "', name='" + name + "', code='" + code + "'}";
    }
}
