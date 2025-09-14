package com.bestprograteam.canvas_dashboard.model.entities;

import java.time.LocalDateTime;

/**
 * Representa una entrega (submission) de un estudiante en el Canvas Dashboard.
 * Guarda datos básicos y además información de seguimiento como fecha de calificación
 * y si la entrega fue tardía.
 */
public class Submission {

    private Integer assignmentId;   // Referencia a la asignación
    private Double score;           // Puntos obtenidos (null si no se entregó)
    private String workflowState;   // Estado de la entrega (ej. "submitted", "unsubmitted")

    // --- Nuevos campos en este commit ---
    private LocalDateTime gradedAt; // Fecha/hora en que fue calificada
    private Boolean late;           // Indica si la entrega fue tardía

    // --- Constructores ---
    public Submission() {}

    public Submission(Integer assignmentId, Double score, String workflowState, LocalDateTime gradedAt, Boolean late) {
        this.assignmentId = assignmentId;
        this.score = score;
        this.workflowState = workflowState;
        this.gradedAt = gradedAt;
        this.late = late;
    }

    // --- Getters y Setters ---
    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(String workflowState) {
        this.workflowState = workflowState;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }

    public Boolean getLate() {
        return late;
    }

    public void setLate(Boolean late) {
        this.late = late;
    }

    // --- toString ---
    @Override
    public String toString() {
        return "Submission{" +
                "assignmentId=" + assignmentId +
                ", score=" + score +
                ", workflowState='" + workflowState + '\'' +
                ", gradedAt=" + gradedAt +
                ", late=" + late +
                '}';
    }
}

