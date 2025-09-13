package com.bestprograteam.canvas_dashboard.model.entities;

import java.util.Date;

public class Submission {
    private String id;
    private String assignmentId;
    private String userId;
    private Date submittedAt;

    public Submission(String id, String assignmentId, String userId, Date submittedAt) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.userId = userId;
        this.submittedAt = submittedAt;
    }

    public boolean isLate() {
        // lógica
        return false;
    }

    public boolean isMissing() {
        // lógica
        return false;
    }

    public boolean isGraded() {
        // lógica
        return false;
    }

    public float getScore() {
        return 0;
    }
}
