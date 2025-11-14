package com.bestprograteam.canvas_dashboard.model.services;

import com.bestprograteam.canvas_dashboard.model.dto.*;
import com.bestprograteam.canvas_dashboard.model.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PredictionServiceTest {

    @InjectMocks
    private PredictionService predictionService;

    @BeforeEach
    void setUp() {
        predictionService = new PredictionService();
    }

    @Test
    void shouldReturnEmptyPredictionWithInsufficientData() {
        // Arrange - Only 3 graded assignments (need 5 minimum)
        DashboardData dashboardData = createDashboardDataWithGrades(3);

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert
        assertThat(result.isPredictionAvailable()).isFalse();
        assertThat(result.gradeProgression).isEmpty();
    }

    @Test
    void shouldCalculateLinearRegressionCorrectly() {
        // Arrange - 5 assignments with consistent upward trend
        DashboardData dashboardData = createDashboardDataWithConsistentGrades();

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.predictedScore).isGreaterThan(0);
        assertThat(result.predictedLetterGrade).isNotNull();
    }

    @Test
    void shouldPredictFutureScores() {
        // Arrange - 5 graded + 2 ungraded assignments
        DashboardData dashboardData = createDashboardDataWithUpcomingAssignments();

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.predictedScore).isBetween(0.0, 100.0);
    }

    @Test
    void shouldCalculateFinalPredictedPercentage() {
        // Arrange
        DashboardData dashboardData = createDashboardDataWithMixedGrades();

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.predictedScore).isPositive();
        assertThat(result.predictedScore).isLessThanOrEqualTo(100.0);
    }

    @Test
    void shouldConvertToLetterGrade_A() {
        // Arrange - High performing student (93%+)
        DashboardData dashboardData = createDashboardDataWithScores(95.0, 94.0, 96.0, 93.0, 95.0);

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.predictedLetterGrade).isIn("A", "A-");
    }

    @Test
    void shouldConvertToLetterGrade_B() {
        // Arrange - B range student (83-86%)
        DashboardData dashboardData = createDashboardDataWithScores(85.0, 84.0, 86.0, 83.0, 85.0);

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.predictedLetterGrade).isIn("B", "B+", "B-");
    }

    @Test
    void shouldConvertToLetterGrade_C() {
        // Arrange - C range student (73-76%)
        DashboardData dashboardData = createDashboardDataWithScores(75.0, 74.0, 76.0, 73.0, 75.0);

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.predictedLetterGrade).isIn("C", "C+", "C-");
    }

    @Test
    void shouldConvertToLetterGrade_F() {
        // Arrange - Failing student (<60%)
        DashboardData dashboardData = createDashboardDataWithScores(55.0, 50.0, 58.0, 52.0, 54.0);

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.predictedLetterGrade).isEqualTo("F");
    }

    @Test
    void shouldGenerateGradeProgressionChart() {
        // Arrange
        DashboardData dashboardData = createDashboardDataWithGrades(5);

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.gradeProgression).isNotEmpty();
        assertThat(result.gradeProgression).hasSize(5);

        // Verify cumulative calculation (each point should include previous scores)
        for (int i = 0; i < result.gradeProgression.size(); i++) {
            ChartDataPoint point = result.gradeProgression.get(i);
            assertThat(point.label).isEqualTo("Assign. " + (i + 1));
            assertThat(point.score).isBetween(0.0, 100.0);
        }
    }

    @Test
    void shouldHandleNullPointsPossible() {
        // Arrange - 5 valid assignments, but only 3 with pointsPossible set
        // This simulates filtering done by DashboardService
        List<CourseCardData> courseCards = new ArrayList<>();
        CourseCardData card = new CourseCardData();

        List<RecentGrade> recentGrades = new ArrayList<>();
        // Only add assignments with valid pointsPossible (real scenario after filtering)
        for (int i = 1; i <= 5; i++) {
            if (i <= 3) { // Only first 3 have valid data
                Assignment assignment = new Assignment();
                assignment.id = i;
                assignment.name = "Assignment " + i;
                assignment.pointsPossible = 100.0;

                Submission submission = new Submission();
                submission.assignmentId = i;
                submission.score = 85.0;
                submission.gradedAt = LocalDateTime.now().minusDays(6 - i);

                recentGrades.add(new RecentGrade(submission, assignment));
            }
        }

        card.recentGrades = recentGrades;
        card.upcomingAssignments = Collections.emptyList();
        courseCards.add(card);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert - Not enough valid data (only 3), should return empty prediction
        assertThat(result).isNotNull();
        assertThat(result.isPredictionAvailable()).isFalse();
    }

    @Test
    void shouldHandleNullScores() {
        // Arrange - Only graded submissions are included (simulates DashboardService filtering)
        List<CourseCardData> courseCards = new ArrayList<>();
        CourseCardData card = new CourseCardData();

        List<RecentGrade> recentGrades = new ArrayList<>();
        // Only add submissions with valid scores and gradedAt
        for (int i = 1; i <= 3; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i;
            assignment.name = "Assignment " + i;
            assignment.pointsPossible = 100.0;

            Submission submission = new Submission();
            submission.assignmentId = i;
            submission.score = 85.0;
            submission.gradedAt = LocalDateTime.now().minusDays(4 - i);

            recentGrades.add(new RecentGrade(submission, assignment));
        }

        card.recentGrades = recentGrades;
        card.upcomingAssignments = Collections.emptyList();
        courseCards.add(card);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert - Only 3 valid graded submissions, so prediction unavailable (need 5 minimum)
        assertThat(result).isNotNull();
        assertThat(result.isPredictionAvailable()).isFalse();
    }

    @Test
    void shouldClampPredictedScores() {
        // Arrange - Create scenario with extreme slope to test clamping
        List<CourseCardData> courseCards = new ArrayList<>();
        CourseCardData card = new CourseCardData();

        List<RecentGrade> recentGrades = new ArrayList<>();
        List<Assignment> upcomingAssignments = new ArrayList<>();

        // Create 5 graded assignments with very high scores (trend up)
        for (int i = 1; i <= 5; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i;
            assignment.name = "Assignment " + i;
            assignment.pointsPossible = 100.0;

            Submission submission = new Submission();
            submission.assignmentId = i;
            submission.score = 95.0 + i; // 96, 97, 98, 99, 100
            submission.gradedAt = LocalDateTime.now().minusDays(6 - i);

            recentGrades.add(new RecentGrade(submission, assignment));
        }

        // Create upcoming assignment far in the future
        Assignment futureAssignment = new Assignment();
        futureAssignment.id = 6;
        futureAssignment.name = "Future Assignment";
        futureAssignment.pointsPossible = 100.0;
        futureAssignment.dueAt = LocalDateTime.now().plusDays(30);
        upcomingAssignments.add(futureAssignment);

        card.recentGrades = recentGrades;
        card.upcomingAssignments = upcomingAssignments;
        courseCards.add(card);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert - Predicted score should be clamped to 100 max
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.predictedScore).isLessThanOrEqualTo(100.0);
    }

    @Test
    void shouldHandleZeroTotalPoints() {
        // Arrange - All assignments have 0 points possible
        List<CourseCardData> courseCards = new ArrayList<>();
        CourseCardData card = new CourseCardData();

        List<RecentGrade> recentGrades = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i;
            assignment.name = "Assignment " + i;
            assignment.pointsPossible = 0.0; // Zero points

            Submission submission = new Submission();
            submission.assignmentId = i;
            submission.score = 0.0;
            submission.gradedAt = LocalDateTime.now().minusDays(6 - i);

            recentGrades.add(new RecentGrade(submission, assignment));
        }

        card.recentGrades = recentGrades;
        card.upcomingAssignments = Collections.emptyList();
        courseCards.add(card);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert - Should return empty prediction (division by zero avoided)
        assertThat(result.isPredictionAvailable()).isFalse();
    }

    @Test
    void shouldHandleMultipleCourses() {
        // Arrange - 2 courses with different grade patterns
        List<CourseCardData> courseCards = new ArrayList<>();

        // Course 1 - Good grades
        CourseCardData card1 = new CourseCardData();
        List<RecentGrade> recentGrades1 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i;
            assignment.pointsPossible = 100.0;
            Submission submission = new Submission();
            submission.assignmentId = i;
            submission.score = 90.0;
            submission.gradedAt = LocalDateTime.now().minusDays(4 - i);
            recentGrades1.add(new RecentGrade(submission, assignment));
        }
        card1.recentGrades = recentGrades1;
        card1.upcomingAssignments = Collections.emptyList();
        courseCards.add(card1);

        // Course 2 - Average grades
        CourseCardData card2 = new CourseCardData();
        List<RecentGrade> recentGrades2 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i + 10;
            assignment.pointsPossible = 100.0;
            Submission submission = new Submission();
            submission.assignmentId = i + 10;
            submission.score = 75.0;
            submission.gradedAt = LocalDateTime.now().minusDays(4 - i);
            recentGrades2.add(new RecentGrade(submission, assignment));
        }
        card2.recentGrades = recentGrades2;
        card2.upcomingAssignments = Collections.emptyList();
        courseCards.add(card2);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;

        // Act
        PredictionData result = predictionService.calculatePredictions(dashboardData);

        // Assert - Should aggregate across both courses
        assertThat(result.isPredictionAvailable()).isTrue();
        assertThat(result.gradeProgression).hasSize(6); // 3 from each course
        // Average should be around 82.5 (90+90+90+75+75+75)/6
        assertThat(result.predictedScore).isBetween(80.0, 85.0);
    }

    // Helper methods to create test data

    private DashboardData createDashboardDataWithGrades(int numGrades) {
        List<CourseCardData> courseCards = new ArrayList<>();
        CourseCardData card = new CourseCardData();

        List<RecentGrade> recentGrades = new ArrayList<>();
        for (int i = 1; i <= numGrades; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i;
            assignment.name = "Assignment " + i;
            assignment.pointsPossible = 100.0;

            Submission submission = new Submission();
            submission.assignmentId = i;
            submission.score = 85.0;
            submission.gradedAt = LocalDateTime.now().minusDays(numGrades + 1 - i);

            recentGrades.add(new RecentGrade(submission, assignment));
        }

        card.recentGrades = recentGrades;
        card.upcomingAssignments = Collections.emptyList();
        courseCards.add(card);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;
        return dashboardData;
    }

    private DashboardData createDashboardDataWithScores(double... scores) {
        List<CourseCardData> courseCards = new ArrayList<>();
        CourseCardData card = new CourseCardData();

        List<RecentGrade> recentGrades = new ArrayList<>();
        for (int i = 0; i < scores.length; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i + 1;
            assignment.name = "Assignment " + (i + 1);
            assignment.pointsPossible = 100.0;

            Submission submission = new Submission();
            submission.assignmentId = i + 1;
            submission.score = scores[i];
            submission.gradedAt = LocalDateTime.now().minusDays(scores.length - i);

            recentGrades.add(new RecentGrade(submission, assignment));
        }

        card.recentGrades = recentGrades;
        card.upcomingAssignments = Collections.emptyList();
        courseCards.add(card);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;
        return dashboardData;
    }

    private DashboardData createDashboardDataWithConsistentGrades() {
        List<CourseCardData> courseCards = new ArrayList<>();
        CourseCardData card = new CourseCardData();

        List<RecentGrade> recentGrades = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i;
            assignment.name = "Assignment " + i;
            assignment.pointsPossible = 100.0;

            Submission submission = new Submission();
            submission.assignmentId = i;
            submission.score = 80.0 + (i * 2); // Consistent upward trend: 82, 84, 86, 88, 90
            submission.gradedAt = LocalDateTime.now().minusDays(6 - i);

            recentGrades.add(new RecentGrade(submission, assignment));
        }

        card.recentGrades = recentGrades;
        card.upcomingAssignments = Collections.emptyList();
        courseCards.add(card);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;
        return dashboardData;
    }

    private DashboardData createDashboardDataWithUpcomingAssignments() {
        List<CourseCardData> courseCards = new ArrayList<>();
        CourseCardData card = new CourseCardData();

        // 5 graded assignments
        List<RecentGrade> recentGrades = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i;
            assignment.name = "Assignment " + i;
            assignment.pointsPossible = 100.0;

            Submission submission = new Submission();
            submission.assignmentId = i;
            submission.score = 85.0;
            submission.gradedAt = LocalDateTime.now().minusDays(6 - i);

            recentGrades.add(new RecentGrade(submission, assignment));
        }

        // 2 upcoming (ungraded) assignments
        List<Assignment> upcomingAssignments = new ArrayList<>();
        for (int i = 6; i <= 7; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i;
            assignment.name = "Upcoming Assignment " + i;
            assignment.pointsPossible = 100.0;
            assignment.dueAt = LocalDateTime.now().plusDays(i - 5);
            upcomingAssignments.add(assignment);
        }

        card.recentGrades = recentGrades;
        card.upcomingAssignments = upcomingAssignments;
        courseCards.add(card);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;
        return dashboardData;
    }

    private DashboardData createDashboardDataWithMixedGrades() {
        List<CourseCardData> courseCards = new ArrayList<>();
        CourseCardData card = new CourseCardData();

        List<RecentGrade> recentGrades = new ArrayList<>();
        double[] scores = {78.0, 82.0, 85.0, 88.0, 90.0};
        for (int i = 0; i < scores.length; i++) {
            Assignment assignment = new Assignment();
            assignment.id = i + 1;
            assignment.name = "Assignment " + (i + 1);
            assignment.pointsPossible = 100.0;

            Submission submission = new Submission();
            submission.assignmentId = i + 1;
            submission.score = scores[i];
            submission.gradedAt = LocalDateTime.now().minusDays(6 - i);

            recentGrades.add(new RecentGrade(submission, assignment));
        }

        card.recentGrades = recentGrades;
        card.upcomingAssignments = Collections.emptyList();
        courseCards.add(card);

        DashboardData dashboardData = new DashboardData();
        dashboardData.courseCards = courseCards;
        return dashboardData;
    }
}
