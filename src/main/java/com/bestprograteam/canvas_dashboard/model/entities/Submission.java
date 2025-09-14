package com.bestprograteam.canvas_dashboard.model.entities;

/**
 * Represents a student submission for an assignment in the Canvas Dashboard.
 * Tracks scores and submission workflow state.
 */
public class Submission {

    private Integer assignmentId;  // Reference to Assignment
    private Double score;          // Points earned (null if unsubmitted)
    private String workflowState;  // Submission status filter

    // Constructors
    public Submission() {}

    public Submission(Integer assignmentId, Double score, String workflowState) {
        this.assignmentId = assignmentId;
        this.score = score;
        this.workflowState = workflowState;
    }

    // Getters and Setters
    public Integer getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Integer assignmentId) { this.assignmentId = assignmentId; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getWorkflowState() { return workflowState; }
    public void setWorkflowState(String workflowState) { this.workflowState = workflowState; }

    @Override
    public String toString() {
        return "Submission{" +
                "assignmentId=" + assignmentId +
                ", score=" + score +
                ", workflowState='" + workflowState + '\'' +
                '}';
    }
}

