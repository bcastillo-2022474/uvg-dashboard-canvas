package com.bestprograteam.canvas_dashboard.model.dto;

/**
 * DTO for raw points summary calculations.
 * Shows earned points vs total possible points with percentage.
 */
public class PointsSummary {

    private Integer pointsEarned;
    private Integer pointsTotal;
    private Double percentage;

    public PointsSummary() {}

    public PointsSummary(Integer pointsEarned, Integer pointsTotal, Double percentage) {
        this.pointsEarned = pointsEarned;
        this.pointsTotal = pointsTotal;
        this.percentage = percentage;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Integer getPointsTotal() {
        return pointsTotal;
    }

    public void setPointsTotal(Integer pointsTotal) {
        this.pointsTotal = pointsTotal;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "PointsSummary{" +
                "pointsEarned=" + pointsEarned +
                ", pointsTotal=" + pointsTotal +
                ", percentage=" + percentage +
                '}';
    }
}