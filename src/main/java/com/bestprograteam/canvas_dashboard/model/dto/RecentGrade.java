package com.bestprograteam.canvas_dashboard.model.dto;

import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import com.bestprograteam.canvas_dashboard.model.entities.Submission;

/**
 * DTO that combines a Submission with its corresponding Assignment details.
 * Used for displaying recent grades with assignment context.
 */
public class RecentGrade {

    private Submission submission;
    private Assignment assignment;

    public RecentGrade() {}

    public RecentGrade(Submission submission, Assignment assignment) {
        this.submission = submission;
        this.assignment = assignment;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    @Override
    public String toString() {
        return "RecentGrade{" +
                "submission=" + submission +
                ", assignment=" + assignment +
                '}';
    }
}
