package com.bestprograteam.canvas_dashboard.service;

import com.bestprograteam.canvas_dashboard.model.dto.PointsSummary;
import com.bestprograteam.canvas_dashboard.model.dto.RecentGrade;
import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import com.bestprograteam.canvas_dashboard.model.entities.Submission;
import com.bestprograteam.canvas_dashboard.model.repositories.AssignmentRepository;
import com.bestprograteam.canvas_dashboard.model.repositories.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for grade-related business logic.
 * Handles recent grades, grade trends, and points calculations.
 */
@Service
public class GradeService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;

    public GradeService(SubmissionRepository submissionRepository,
                       AssignmentRepository assignmentRepository) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Get the 5 most recent grades for a course.
     * Combines submission data with assignment details for display.
     * @param courseId Canvas course ID
     * @return List of recent submissions with assignment details
     */
    public List<RecentGrade> getRecentGrades(Integer courseId) {
        List<Submission> recentSubmissions = submissionRepository.findRecentGrades(courseId, 5);
        List<RecentGrade> recentGrades = new ArrayList<>();

        for (Submission submission : recentSubmissions) {
            Assignment assignment = assignmentRepository.findAssignmentById(
                    courseId,
                    submission.getAssignmentId()
            );
            recentGrades.add(new RecentGrade(submission, assignment));
        }

        return recentGrades;
    }

    /**
     * Calculate grade trend for a course (up/down/stable).
     * Compares recent submission percentages to determine trend.
     * @param courseId Canvas course ID
     * @return Trend string: "up", "down", or "stable"
     */
    public String calculateGradeTrend(Integer courseId) {
        List<Submission> recentSubmissions = submissionRepository.findRecentGrades(courseId, 10);

        if (recentSubmissions.size() < 2) {
            return "stable";
        }

        // Get percentages for recent submissions
        List<Double> percentages = new ArrayList<>();
        for (Submission submission : recentSubmissions) {
            if (submission.getScore() != null) {
                Assignment assignment = assignmentRepository.findAssignmentById(
                        courseId,
                        submission.getAssignmentId()
                );
                if (assignment != null && assignment.getPointsPossible() != null && assignment.getPointsPossible() > 0) {
                    double percentage = (submission.getScore() / assignment.getPointsPossible()) * 100;
                    percentages.add(percentage);
                }
            }
        }

        if (percentages.size() < 2) {
            return "stable";
        }

        // Compare first half vs second half averages
        int midpoint = percentages.size() / 2;
        double firstHalfAvg = percentages.subList(0, midpoint).stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        double secondHalfAvg = percentages.subList(midpoint, percentages.size()).stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double difference = secondHalfAvg - firstHalfAvg;

        if (difference > 5.0) {
            return "up";
        } else if (difference < -5.0) {
            return "down";
        } else {
            return "stable";
        }
    }

    /**
     * Calculate raw points summary for a course.
     * Sums assignment points and submission scores.
     * @param courseId Canvas course ID
     * @return PointsSummary object with earned/total/percentage
     */
    public PointsSummary calculatePointsSummary(Integer courseId) {
        List<Assignment> assignments = assignmentRepository.findAssignmentsByCourseId(courseId);
        List<Submission> submissions = submissionRepository.findSubmissionsByCourseId(courseId);

        int totalPoints = 0;
        int earnedPoints = 0;

        for (Assignment assignment : assignments) {
            if (assignment.getPointsPossible() != null) {
                totalPoints += assignment.getPointsPossible().intValue();

                Submission submission = submissions.stream()
                        .filter(s -> s.getAssignmentId().equals(assignment.getId()))
                        .findFirst()
                        .orElse(null);

                if (submission != null && submission.getScore() != null) {
                    earnedPoints += submission.getScore().intValue();
                }
            }
        }

        double percentage = totalPoints > 0 ? (earnedPoints * 100.0) / totalPoints : 0.0;

        return new PointsSummary(earnedPoints, totalPoints, percentage);
    }

    public PointsSummary calculateGradedPointsSummary(Integer courseId) {
        List<Assignment> assignments = assignmentRepository.findAssignmentsByCourseId(courseId);
        List<Submission> submissions = submissionRepository.findSubmissionsByCourseId(courseId);

        int totalGradedPoints = 0;
        int earnedGradedPoints = 0;

        for (Assignment assignment : assignments) {
            if (assignment.getPointsPossible() != null) {
                Submission submission = submissions.stream()
                        .filter(s -> s.getAssignmentId().equals(assignment.getId()))
                        .findFirst()
                        .orElse(null);

                if (submission != null && submission.isGraded() && submission.getScore() != null) {
                    totalGradedPoints += assignment.getPointsPossible().intValue();
                    earnedGradedPoints += submission.getScore().intValue();
                }
            }
        }

        double percentage = totalGradedPoints > 0 ? (earnedGradedPoints * 100.0) / totalGradedPoints : 0.0;

        return new PointsSummary(earnedGradedPoints, totalGradedPoints, percentage);
    }

    public Integer getCompletedAssignmentCount(Integer courseId) {
        List<Submission> submissions = submissionRepository.findSubmissionsByCourseId(courseId);
        return (int) submissions.stream()
                .filter(Submission::isGraded)
                .count();
    }
}
