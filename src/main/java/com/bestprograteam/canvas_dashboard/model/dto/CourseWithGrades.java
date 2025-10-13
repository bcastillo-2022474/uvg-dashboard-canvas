package com.bestprograteam.canvas_dashboard.model.dto;

import com.bestprograteam.canvas_dashboard.model.entities.Course;
import com.bestprograteam.canvas_dashboard.model.entities.Enrollment;

/**
 * DTO that combines Course information with Enrollment grades.
 * Used for dashboard display showing course details alongside Canvas-computed grades.
 */
public class CourseWithGrades {

    private Course course;
    private Enrollment enrollment;

    public CourseWithGrades() {}

    public CourseWithGrades(Course course, Enrollment enrollment) {
        this.course = course;
        this.enrollment = enrollment;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
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
