package com.bestprograteam.canvas_dashboard.model.entities;

public class AssignmentGroup {
    public Integer id;
    public Integer courseId;
    public String name;
    public Integer position;
    public Double groupWeight;

    public AssignmentGroup() {}

    public AssignmentGroup(Integer id, Integer courseId, String name, Integer position, Double groupWeight) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.position = position;
        this.groupWeight = groupWeight;
    }

    @Override
    public String toString() {
        return "AssignmentGroup{id=" + id + ", name='" + name + "', groupWeight=" + groupWeight + "}";
    }
}
