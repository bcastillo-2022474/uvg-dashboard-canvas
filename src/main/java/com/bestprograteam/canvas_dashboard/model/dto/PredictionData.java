package com.bestprograteam.canvas_dashboard.model.dto;

import java.util.List;

public class PredictionData {
    private double predictedScore;
    private String predictedLetterGrade;
    private List<ChartDataPoint> gradeProgression;
    private boolean isPredictionAvailable;

    // Constructors
    public PredictionData() {
        this.isPredictionAvailable = false;
    }

    public PredictionData(double predictedScore, String predictedLetterGrade, List<ChartDataPoint> gradeProgression) {
        this.predictedScore = predictedScore;
        this.predictedLetterGrade = predictedLetterGrade;
        this.gradeProgression = gradeProgression;
        this.isPredictionAvailable = true;
    }

    // Getters and Setters
    public double getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(double predictedScore) {
        this.predictedScore = predictedScore;
    }

    public String getPredictedLetterGrade() {
        return predictedLetterGrade;
    }

    public void setPredictedLetterGrade(String predictedLetterGrade) {
        this.predictedLetterGrade = predictedLetterGrade;
    }

    public List<ChartDataPoint> getGradeProgression() {
        return gradeProgression;
    }

    public void setGradeProgression(List<ChartDataPoint> gradeProgression) {
        this.gradeProgression = gradeProgression;
    }

    public boolean isPredictionAvailable() {
        return isPredictionAvailable;
    }

    public void setPredictionAvailable(boolean predictionAvailable) {
        isPredictionAvailable = predictionAvailable;
    }
}
