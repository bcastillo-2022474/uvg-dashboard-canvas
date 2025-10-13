package com.bestprograteam.canvas_dashboard.model.dto;

public class ChartDataPoint {
    private String label;
    private double score;

    public ChartDataPoint(String label, double score) {
        this.label = label;
        this.score = score;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ChartDataPoint{" +
                "label='" + label + '\'' +
                ", score=" + score +
                '}';
    }
}
