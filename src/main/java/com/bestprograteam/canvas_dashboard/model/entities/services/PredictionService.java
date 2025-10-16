package com.bestprograteam.canvas_dashboard.model.services;

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

@Service
public class PredictionService {

    private record DataPoint(double x, double y) {}

    public PredictionData calculatePredictions(DashboardData dashboardData) {
        List<Assignment> allAssignments = new ArrayList<>();
        List<RecentGrade> gradedRecentGrades = new ArrayList<>();

        dashboardData.courseCards.forEach(courseCard -> {
            gradedRecentGrades.addAll(courseCard.recentGrades);
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

        gradedRecentGrades.sort(Comparator.comparing(rg -> rg.submission.gradedAt));

        if (gradedRecentGrades.size() < 5) {
            return new PredictionData(); 
        }

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
            return new PredictionData(); 
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
            return new PredictionData();
        }
        double slope = (n * sumXY - sumX * sumY) / denominator;
        double intercept = (sumY - slope * sumX) / n;

        return new PredictionData();
    }
}
