package com.bestprograteam.canvas_dashboard.model.entities;

import java.time.LocalDateTime;

/**
 * Representa una entrega (submission) de un estudiante en el Canvas Dashboard.
 * Se utiliza para registrar la nota obtenida, el estado de la entrega,
 * la fecha de calificación y si fue entregada tarde.
 */
public class Submission {

    // --- Campos principales ---
    private Integer assignmentId;   // Referencia a la asignación
    private Double score;           // Puntos obtenidos (null si no se entregó)
    private String workflowState;   // Estado de la entrega (ej. "submitted", "unsubmitted")

    // --- Campos de seguimiento ---
    private LocalDateTime gradedAt; // Fecha/hora en que fue calificada
    private Boolean late;           // Indica si la entrega fue tardía

    // --- Constructores ---
    public Submission() {}

    public Submission(Integer assignmentId, Double score, String workflowState,LocalDateTime gradedAt, Boolean late) {
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

    // --- Métodos auxiliares ---
    /**
     * Indica si la entrega ya fue calificada.
     * @return true si gradedAt no es null, false en caso contrario
     */
    public boolean isGraded() {
        return gradedAt != null;
    }

    /**
     * Indica si la entrega fue entregada tarde.
     * @return true si late es true, false en caso contrario
     */
    public boolean isLate() {
        return Boolean.TRUE.equals(late);
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

