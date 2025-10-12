package com.bestprograteam.canvas_dashboard.model.entities;

/**
 * Represents an assignment group (grade category) in the Canvas Dashboard.
 * Used for calculating weighted grades by category (Exams, Labs, Homework, etc.)
 */
public class AssignmentGroup {

    // --- Canvas API Fields ---
    private Integer id;
    private Integer courseId;
    private String name;
    private Integer position;
    private Double groupWeight;

    // --- Constructors ---
    public AssignmentGroup() {}

    public AssignmentGroup(Integer id, Integer courseId, String name, Integer position, Double groupWeight) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.position = position;
        this.groupWeight = groupWeight;
    }

    // --- Getters and Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Double getGroupWeight() {
        return groupWeight;
    }

    public void setGroupWeight(Double groupWeight) {
        this.groupWeight = groupWeight;
    }

    @Override
    public String toString() {
        return "AssignmentGroup{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", groupWeight=" + groupWeight +
                '}';
    }
}
