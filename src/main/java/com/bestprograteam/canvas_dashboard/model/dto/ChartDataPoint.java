package com.bestprograteam.canvas_dashboard.model.dto;

public class ChartDataPoint {
    public String label;
    public double score;

    public ChartDataPoint(String label, double score) {
        this.label = label;
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