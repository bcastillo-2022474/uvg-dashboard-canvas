package com.bestprograteam.canvas_dashboard.model.services;

import com.bestprograteam.canvas_dashboard.model.dto.ChartDataPoint;
import com.bestprograteam.canvas_dashboard.model.dto.DashboardData;
import com.bestprograteam.canvas_dashboard.model.dto.PredictionData;
import com.bestprograteam.canvas_dashboard.model.dto.RecentGrade;
import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private record DataPoint(double x, double y) {}

    public PredictionData calculatePredictions(DashboardData dashboardData) {
        List<Assignment> allAssignments = new ArrayList<>();
        List<RecentGrade> gradedRecentGrades = new ArrayList<>();

        dashboardData.courseCards.forEach(courseCard -> {
            gradedRecentGrades.addAll(courseCard.recentGrades);

            // Add all assignments to a single list, avoiding duplicates
            courseCard.recentGrades.forEach(rg -> {
                if (allAssignments.stream().noneMatch(a -> a.id.equals(rg.assignment.id))) {
                    allAssignments.add(rg.assignment);
                }
            });
            courseCard.upcomingAssignments.forEach(ua -> {
                if (allAssignments.stream().noneMatch(a -> a.id.equals(ua.id))) {
                    allAssignments.add(ua);
                }
            });
        });

        // Sort graded assignments chronologically for processing
        gradedRecentGrades.sort(Comparator.comparing(rg -> rg.submission.gradedAt));

        if (gradedRecentGrades.size() < 5) {
            return new PredictionData(); // Not enough data
        }

        // Generate Grade Progression Chart Data
        List<ChartDataPoint> gradeProgression = generateGradeProgression(gradedRecentGrades);

        // --- Linear Regression Calculation ---
        Instant firstGradedDate = gradedRecentGrades.get(0).submission.gradedAt.atZone(ZoneOffset.UTC).toInstant();
        List<DataPoint> dataPoints = new ArrayList<>();
        for (RecentGrade rg : gradedRecentGrades) {
            Assignment assignment = rg.assignment;
            if (assignment.pointsPossible > 0 && rg.submission.score != null) {
                double x = Duration.between(firstGradedDate, rg.submission.gradedAt.atZone(ZoneOffset.UTC).toInstant()).toDays();
                double y = (rg.submission.score / assignment.pointsPossible) * 100;
                dataPoints.add(new DataPoint(x, y));
            }
        }

        if (dataPoints.size() < 5) {
            return new PredictionData(); // Not enough valid data points for regression
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (DataPoint p : dataPoints) {
            sumX += p.x();
            sumY += p.y();
            sumXY += p.x() * p.y();
            sumX2 += p.x() * p.x();
        }

        int n = dataPoints.size();
        double denominator = n * sumX2 - Math.pow(sumX, 2);
        if (denominator == 0) {
            return new PredictionData(); // Avoid division by zero (all x values are the same)
        }
        double slope = (n * sumXY - sumX * sumY) / denominator;
        double intercept = (sumY - slope * sumX) / n;

        // --- Predict Future Scores ---
        List<Integer> gradedAssignmentIds = gradedRecentGrades.stream()
                .map(rg -> rg.assignment.id)
                .toList();

        List<Assignment> ungradedAssignments = allAssignments.stream()
                .filter(a -> !gradedAssignmentIds.contains(a.id))
                .toList();

        double totalPredictedPoints = 0;
        for (Assignment assignment : ungradedAssignments) {
            if (assignment.pointsPossible > 0) {
                Instant predictionTime = assignment.dueAt != null ? assignment.dueAt.atZone(ZoneOffset.UTC).toInstant() : Instant.now();
                double x = Duration.between(firstGradedDate, predictionTime).toDays();
                double predictedScorePercentage = slope * x + intercept;
                predictedScorePercentage = Math.max(0, Math.min(100, predictedScorePercentage)); // Clamp
                totalPredictedPoints += (predictedScorePercentage / 100) * assignment.pointsPossible;
            }
        }

        // --- Calculate Final Grade ---
        double totalEarnedPoints = gradedRecentGrades.stream()
                .mapToDouble(rg -> rg.submission.score != null ? rg.submission.score : 0)
                .sum();

        double totalPossiblePoints = allAssignments.stream()
                .mapToDouble(a -> a.pointsPossible)
                .sum();

        if (totalPossiblePoints == 0) {
            return new PredictionData();
        }

        System.out.println(totalEarnedPoints + " / " + totalPossiblePoints);
        double finalPredictedPercentage = ((totalEarnedPoints + totalPredictedPoints) / totalPossiblePoints) * 100;
        String letterGrade = convertToLetterGrade(finalPredictedPercentage);

        System.out.println(gradeProgression);
        System.out.println(finalPredictedPercentage);
        System.out.println(letterGrade);
        return new PredictionData(finalPredictedPercentage, letterGrade, gradeProgression);
    }

    private List<ChartDataPoint> generateGradeProgression(List<RecentGrade> gradedRecentGrades) {
        List<ChartDataPoint> progression = new ArrayList<>();
        double cumulativeScore = 0;
        double cumulativePossiblePoints = 0;
        int assignmentCount = 0;

        for (RecentGrade rg : gradedRecentGrades) {
            if (rg.submission.score == null || rg.assignment.pointsPossible == null) {
                continue;
            }

            assignmentCount++;
            cumulativeScore += rg.submission.score;
            cumulativePossiblePoints += rg.assignment.pointsPossible;
            if (cumulativePossiblePoints > 0) {
                double currentOverall = (cumulativeScore / cumulativePossiblePoints) * 100;
                progression.add(new ChartDataPoint("Assign. " + assignmentCount, currentOverall));
            }
        }
        return progression;
    }

    private String convertToLetterGrade(double percentage) {
        if (percentage >= 93) return "A";
        if (percentage >= 90) return "A-";
        if (percentage >= 87) return "B+";
        if (percentage >= 83) return "B";
        if (percentage >= 80) return "B-";
        if (percentage >= 77) return "C+";
        if (percentage >= 73) return "C";
        if (percentage >= 70) return "C-";
        if (percentage >= 67) return "D+";
        if (percentage >= 63) return "D";
        if (percentage >= 60) return "D-";
        return "F";
    }
}