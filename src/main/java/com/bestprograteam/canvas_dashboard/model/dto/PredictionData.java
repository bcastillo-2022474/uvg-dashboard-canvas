package com.bestprograteam.canvas_dashboard.model.dto;

import java.util.ArrayList;
import java.util.List;

public class PredictionData {
    public double predictedScore;
    public String predictedLetterGrade;
    public List<ChartDataPoint> gradeProgression;

    private boolean predictionAvailable;

    public PredictionData() {
        this.predictionAvailable = false;
        this.gradeProgression = new ArrayList<>();
    }

    public PredictionData(double predictedScore, String predictedLetterGrade, List<ChartDataPoint> gradeProgression) {
        this.predictedScore = predictedScore;
        this.predictedLetterGrade = predictedLetterGrade;
        this.gradeProgression = gradeProgression;
        this.predictionAvailable = true;
    }

    // Keep this method - it's used in view as th:if="${predictionData.isPredictionAvailable()}"
    public boolean isPredictionAvailable() {
        return predictionAvailable;
    }
}