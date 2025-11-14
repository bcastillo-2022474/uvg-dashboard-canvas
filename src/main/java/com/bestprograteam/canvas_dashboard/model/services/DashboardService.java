package com.bestprograteam.canvas_dashboard.model.services;

import com.bestprograteam.canvas_dashboard.model.dto.*;
import com.bestprograteam.canvas_dashboard.model.entities.*;
import com.bestprograteam.canvas_dashboard.model.repositories.*;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Simplified dashboard service that calls repositories directly.
 * Fetches all data in parallel for maximum performance.
 */
@Service
public class DashboardService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final AssignmentGroupRepository assignmentGroupRepository;

    private final ExecutorService executor;

    public DashboardService(CourseRepository courseRepository,
                            EnrollmentRepository enrollmentRepository,
                            AssignmentRepository assignmentRepository,
                            SubmissionRepository submissionRepository,
                            AssignmentGroupRepository assignmentGroupRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.assignmentGroupRepository = assignmentGroupRepository;

        // Wrap executor to propagate SecurityContext to child threads
        this.executor = new DelegatingSecurityContextExecutorService(
                Executors.newFixedThreadPool(20)
        );
    }


    /**
     * Main entry point - fetches ALL dashboard data in parallel.
     */
    public DashboardData getDashboardData() {
        System.out.println("=== DashboardService START ===");

        // Step 1: Fetch courses and enrollments in parallel
        CompletableFuture<List<Course>> coursesFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("[1] Fetching courses...");
            return courseRepository.findAllActiveCourses();
        }, executor);

        CompletableFuture<List<Enrollment>> enrollmentsFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("[2] Fetching enrollments...");
            return enrollmentRepository.findAllEnrollments();
        }, executor);

        // Wait for courses and enrollments
        CompletableFuture.allOf(coursesFuture, enrollmentsFuture).join();
        List<Course> courses = coursesFuture.join();
        List<Enrollment> enrollments = enrollmentsFuture.join();

        System.out.println("Found " + courses.size() + " courses, " + enrollments.size() + " enrollments");

        // Step 2: For each course, fetch assignments, submissions, and groups in parallel
        List<CompletableFuture<CourseCardData>> cardFutures = courses.stream()
                .map(course -> CompletableFuture.supplyAsync(() -> {
                    Integer courseId = Integer.parseInt(course.id);
                    System.out.println("[Processing] Course " + courseId + " - " + course.name);

                    try {
                        // Fetch all data for this course in parallel
                        CompletableFuture<List<Assignment>> assignmentsFuture = CompletableFuture.supplyAsync(
                                () -> assignmentRepository.findAssignmentsByCourseId(courseId), executor);

                        CompletableFuture<List<Submission>> submissionsFuture = CompletableFuture.supplyAsync(
                                () -> submissionRepository.findSubmissionsByCourseId(courseId), executor);

                        CompletableFuture<List<AssignmentGroup>> groupsFuture = CompletableFuture.supplyAsync(
                                () -> assignmentGroupRepository.findAssignmentGroupsByCourseId(courseId), executor);

                        // Wait for all
                        CompletableFuture.allOf(assignmentsFuture, submissionsFuture, groupsFuture).join();

                        List<Assignment> assignments = assignmentsFuture.join();
                        List<Submission> submissions = submissionsFuture.join();
                        List<AssignmentGroup> groups = groupsFuture.join();

                        // Find enrollment for this course
                        Enrollment enrollment = enrollments.stream()
                                .filter(e -> e.courseId.equals(courseId))
                                .findFirst()
                                .orElse(null);

                        // Build CourseCardData
                        CourseCardData cardData = buildCourseCardData(course, enrollment, assignments, submissions, groups);

                        System.out.println("[✓] Course " + courseId + " complete");
                        return cardData;

                    } catch (Exception e) {
                        System.err.println("[✗] Error processing course " + courseId + ": " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                }, executor))
                .collect(Collectors.toList());

        // Wait for all course cards
        List<CourseCardData> courseCards = cardFutures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        System.out.println("Processed " + courseCards.size() + " course cards");

        // Step 3: Calculate upcoming assignments from all courses
        List<Assignment> allUpcomingAssignments = courseCards.stream()
                .flatMap(card -> card.upcomingAssignments.stream())
                .sorted(Comparator.comparing((Assignment a) -> a.dueAt))
                .limit(10)
                .collect(Collectors.toList());

        // Step 4: Calculate semester summary
        SemesterSummary summary = calculateSemesterSummary(courseCards, enrollments);

        System.out.println("=== DashboardService COMPLETE ===");
        return new DashboardData(courseCards, allUpcomingAssignments, summary);
    }

    /**
     * Builds CourseCardData from raw repository data.
     */
    private CourseCardData buildCourseCardData(Course course, Enrollment enrollment,
                                               List<Assignment> assignments,
                                               List<Submission> submissions,
                                               List<AssignmentGroup> groups) {

        // CourseWithGrades
        CourseWithGrades courseWithGrades = new CourseWithGrades(course, enrollment);

        // Recent grades (last 5)
        List<RecentGrade> recentGrades = submissions.stream()
                .filter(s -> s.gradedAt != null)
                .sorted(Comparator.comparing((Submission s) -> s.gradedAt).reversed())
                .limit(5)
                .map(submission -> {
                    Assignment assignment = assignments.stream()
                            .filter(a -> a.id.equals(submission.assignmentId))
                            .findFirst()
                            .orElse(null);
                    return assignment != null ? new RecentGrade(submission, assignment) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Category breakdown
        List<CategoryBreakdown> categoryBreakdown = groups.stream()
                .map(group -> {
                    List<Assignment> groupAssignments = assignments.stream()
                            .filter(a -> a.assignmentGroupId != null && a.assignmentGroupId.equals(group.id))
                            .collect(Collectors.toList());

                    double earnedPoints = 0.0;
                    double totalPoints = 0.0;
                    int completedCount = 0;

                    for (Assignment assignment : groupAssignments) {
                        if (assignment.pointsPossible != null) {
                            Submission submission = submissions.stream()
                                    .filter(s -> s.assignmentId.equals(assignment.id))
                                    .findFirst()
                                    .orElse(null);

                            if (submission != null && submission.score != null) {
                                earnedPoints += submission.score;
                                totalPoints += assignment.pointsPossible;
                                completedCount++;
                            }
                        }
                    }

                    double currentScore = totalPoints > 0 ? (earnedPoints * 100.0 / totalPoints) : 0.0;
                    return new CategoryBreakdown(group, currentScore, (int) earnedPoints, (int) totalPoints,
                            completedCount, groupAssignments.size());
                })
                .collect(Collectors.toList());

        // Upcoming assignments (next 7 days)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureLimit = now.plusDays(7);
        List<Assignment> upcomingAssignments = assignments.stream()
                .filter(a -> a.dueAt != null)
                .filter(a -> a.dueAt.isAfter(now) && a.dueAt.isBefore(futureLimit))
                .sorted(Comparator.comparing((Assignment a) -> a.dueAt))
                .collect(Collectors.toList());

        // Trend calculation (simple version based on recent grades)
        String trend = calculateTrend(recentGrades);

        // Calculate remaining points
        double totalPossiblePoints = assignments.stream()
                .filter(a -> a.pointsPossible != null)
                .mapToDouble(a -> a.pointsPossible)
                .sum();

        double gradedPointsPossible = 0.0;
        for (Assignment assignment : assignments) {
            if (assignment.pointsPossible != null) {
                boolean isGraded = submissions.stream()
                    .anyMatch(s -> s.assignmentId.equals(assignment.id) && s.score != null);
                if (isGraded) {
                    gradedPointsPossible += assignment.pointsPossible;
                }
            }
        }

        // Normalize to percentage (like PredictionService) so all courses show on same scale
        double remainingPercentage = totalPossiblePoints > 0
            ? ((totalPossiblePoints - gradedPointsPossible) / totalPossiblePoints) * 100
            : 0.0;

        return new CourseCardData(courseWithGrades, recentGrades, categoryBreakdown, upcomingAssignments, trend, remainingPercentage);
    }

    /**
     * Calculate overall semester summary.
     */
    private SemesterSummary calculateSemesterSummary(List<CourseCardData> courseCards, List<Enrollment> enrollments) {
        // Overall percentage (average of current scores)
        double overallPercentage = enrollments.stream()
                .map(e -> e.currentScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        int totalCourses = courseCards.size();

        // Count upcoming assignments
        long upcomingCount = courseCards.stream()
                .flatMap(card -> card.upcomingAssignments.stream())
                .count();

        // Overall trend
        String overallTrend = calculateOverallTrend(courseCards);

        // Aggregate points
        int totalCompletedAssignments = courseCards.stream()
                .mapToInt(card -> card.recentGrades.size())
                .sum();

        double totalGradedPoints = courseCards.stream()
                .flatMap(card -> card.recentGrades.stream())
                .map(rg -> rg.submission)
                .filter(sub -> sub.score != null)
                .mapToDouble(sub -> sub.score)
                .sum();

        double totalGradedPointsPossible = courseCards.stream()
                .flatMap(card -> card.recentGrades.stream())
                .map(rg -> rg.assignment)
                .filter(a -> a.pointsPossible != null)
                .mapToDouble(a -> a.pointsPossible)
                .sum();

        double totalUpcomingPoints = courseCards.stream()
                .flatMap(card -> card.upcomingAssignments.stream())
                .filter(a -> a.pointsPossible != null)
                .mapToDouble(a -> a.pointsPossible)
                .sum();

        double totalSemesterPointsPossible = totalGradedPointsPossible + totalUpcomingPoints;

        double totalGradedPercentage = totalGradedPointsPossible > 0
                ? (totalGradedPoints * 100.0) / totalGradedPointsPossible
                : 0.0;

        SemesterSummary summary = new SemesterSummary(overallPercentage, totalCourses, (int) upcomingCount, overallTrend);
        summary.totalUpcomingPoints = (int) totalUpcomingPoints;
        summary.totalGradedPointsPossible = (int) totalGradedPointsPossible;
        summary.totalGradedPercentage = totalGradedPercentage;
        summary.totalSemesterPointsPossible = (int) totalSemesterPointsPossible;
        summary.totalCompletedAssignments = totalCompletedAssignments;
        summary.totalGradedPoints = (int) totalGradedPoints;

        return summary;
    }

    private String calculateTrend(List<RecentGrade> recentGrades) {
        if (recentGrades.size() < 2) {
            return "stable";
        }

        // Compare first half vs second half of recent grades
        int halfSize = recentGrades.size() / 2;
        double firstHalfAvg = recentGrades.stream()
                .limit(halfSize)
                .filter(g -> g.submission.score != null && g.assignment.pointsPossible != null)
                .mapToDouble(g -> (g.submission.score / g.assignment.pointsPossible) * 100)
                .average()
                .orElse(0.0);

        double secondHalfAvg = recentGrades.stream()
                .skip(halfSize)
                .filter(g -> g.submission.score != null && g.assignment.pointsPossible != null)
                .mapToDouble(g -> (g.submission.score / g.assignment.pointsPossible) * 100)
                .average()
                .orElse(0.0);

        if (firstHalfAvg > secondHalfAvg + 5) {
            return "up";
        } else if (secondHalfAvg > firstHalfAvg + 5) {
            return "down";
        } else {
            return "stable";
        }
    }

    private String calculateOverallTrend(List<CourseCardData> courseCards) {
        if (courseCards.isEmpty()) {
            return "stable";
        }

        int upCount = 0;
        int downCount = 0;

        for (CourseCardData card : courseCards) {
            String trend = card.trend;
            if ("up".equals(trend)) {
                upCount++;
            } else if ("down".equals(trend)) {
                downCount++;
            }
        }

        if (upCount > downCount) {
            return "up";
        } else if (downCount > upCount) {
            return "down";
        } else {
            return "stable";
        }
    }
}
