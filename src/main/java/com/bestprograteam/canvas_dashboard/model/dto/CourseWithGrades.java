package com.bestprograteam.canvas_dashboard.model.dto;

import com.bestprograteam.canvas_dashboard.model.entities.Course;
import com.bestprograteam.canvas_dashboard.model.entities.Enrollment;

/**
 * DTO that combines Course information with Enrollment grades.
 * Used for dashboard display showing course details alongside Canvas-computed grades.
 */
public class CourseWithGrades {

    public Course course;
    public Enrollment enrollment;

    public CourseWithGrades() {}

    public CourseWithGrades(Course course, Enrollment enrollment) {
        this.course = course;
        this.enrollment = enrollment;
    }

    @Override
    public String toString() {
        return "CourseWithGrades{" +
                "course=" + course +
                ", enrollment=" + enrollment +
                '}';
    }
}
