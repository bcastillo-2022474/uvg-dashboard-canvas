package com.bestprograteam.canvas_dashboard.model.entities;

import java.time.LocalDateTime;

public class Submission {
    public Integer assignmentId;
    public Double score;
    public String workflowState;
    public LocalDateTime gradedAt;
    public Boolean late;

    public Submission() {}

    public Submission(Integer assignmentId, Double score, String workflowState,
                     LocalDateTime gradedAt, Boolean late) {
        this.assignmentId = assignmentId;
        this.score = score;
        this.workflowState = workflowState;
        this.gradedAt = gradedAt;
        this.late = late;
    }

    // Keep this method - used in service layer logic
    public boolean isGraded() {
        return gradedAt != null && score != null;
    }

    @Override
    public String toString() {
        return "Submission{assignmentId=" + assignmentId + ", score=" + score + ", gradedAt=" + gradedAt + "}";
    }
}
