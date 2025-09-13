package models;

import java.util.Date;

public class Assignment {
    private String id;
    private String name;
    private String description;
    private Date dueDate;
    private float pointsPossible;
    private String[] submissionTypes;
    private int courseId;
    private String gradingCategoryId;
    private int position;

    public Assignment(String id, String name, String description, Date dueDate, float pointsPossible, String[] submissionTypes, int courseId, String gradingCategoryId, int position) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.pointsPossible = pointsPossible;
        this.submissionTypes = submissionTypes;
        this.courseId = courseId;
        this.gradingCategoryId = gradingCategoryId;
        this.position = position;
    }

    public boolean isOverdue() {
        return new Date().after(dueDate);
    }

    public int getDaysUntilDue() {
        long diff = dueDate.getTime() - new Date().getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public boolean isCompleted(String userId) {
        
        return false;//completado
    }

    public String getStatus() {
        return isOverdue() ? "Overdue" : "Pending";
    }

    public Submission getSubmission(String userId) {
        return null;
    }
}
