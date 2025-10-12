package com.bestprograteam.canvas_dashboard.service;

import com.bestprograteam.canvas_dashboard.model.dto.*;
import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator service for dashboard operations.
 * Aggregates data from all other services to provide complete dashboard views.
 */
@Service
public class DashboardService {

    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final GradeService gradeService;
    private final CategoryService categoryService;

    public DashboardService(CourseService courseService,
                          AssignmentService assignmentService,
                          GradeService gradeService,
                          CategoryService categoryService) {
        this.courseService = courseService;
        this.assignmentService = assignmentService;
        this.gradeService = gradeService;
        this.categoryService = categoryService;
    }

    /**
     * Get complete dashboard data for the home page.
     * Aggregates all necessary data in one call.
     * @return DashboardData object with all dashboard information
     */
    public DashboardData getDashboardData() {
        System.out.println("=== DashboardService.getDashboardData() START ===");

        System.out.println("Step 1: Getting all courses with grades...");
        List<CourseWithGrades> courses = courseService.getAllCoursesWithGrades();
        System.out.println("Found " + courses.size() + " courses");

        List<CourseCardData> courseCards = new ArrayList<>();

        for (int i = 0; i < courses.size(); i++) {
            CourseWithGrades courseWithGrades = courses.get(i);
            if (courseWithGrades.getCourse() != null) {
                Integer courseId = Integer.parseInt(courseWithGrades.getCourse().getId());
                System.out.println("Step 2." + (i+1) + ": Processing course " + courseId + " - " + courseWithGrades.getCourse().getName());

                try {
                    CourseCardData cardData = getCourseCardData(courseId);
                    courseCards.add(cardData);
                    System.out.println("  ✓ Successfully processed course " + courseId);
                } catch (Exception e) {
                    System.err.println("  ✗ Error processing course " + courseId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Step 3: Getting upcoming assignments...");
        List<Assignment> upcomingAssignments = assignmentService.getUpcomingAssignments();
        System.out.println("Found " + upcomingAssignments.size() + " upcoming assignments");

        System.out.println("Step 4: Calculating semester summary...");
        SemesterSummary summary = getSemesterSummary(courseCards);
        System.out.println("Summary calculated");

        System.out.println("=== DashboardService.getDashboardData() COMPLETE ===");
        return new DashboardData(courseCards, upcomingAssignments, summary);
    }

    /**
     * Get detailed data for a single course card.
     * @param courseId Canvas course ID
     * @return CourseCardData with all info for one course
     */
    public CourseCardData getCourseCardData(Integer courseId) {
        System.out.println("    → getCourseCardData for course " + courseId);

        System.out.println("      1. Getting course with grades...");
        CourseWithGrades courseWithGrades = courseService.getCourseWithGrades(courseId);

        System.out.println("      2. Getting recent grades...");
        List<RecentGrade> recentGrades = gradeService.getRecentGrades(courseId);

        System.out.println("      3. Getting category breakdown...");
        List<CategoryBreakdown> categoryBreakdown = categoryService.getCategoryBreakdown(courseId);

        System.out.println("      4. Getting upcoming assignments...");
        List<Assignment> upcomingAssignments = assignmentService.getUpcomingAssignmentsByCourse(courseId, 7);

        System.out.println("      5. Calculating trend...");
        String trend = gradeService.calculateGradeTrend(courseId);

        System.out.println("    ← getCourseCardData complete for course " + courseId);
        return new CourseCardData(
                courseWithGrades,
                recentGrades,
                categoryBreakdown,
                upcomingAssignments,
                trend
        );
    }

    /**
     * Get semester summary statistics for top widgets.
     * Aggregates data from all courses for summary widgets.
     * @return SemesterSummary with overall stats
     */
    private SemesterSummary getSemesterSummary(List<CourseCardData> courseCards) {
        Double overallPercentage = courseService.calculateOverallPercentage();
        Integer totalCourses = courseCards.size();

        List<Assignment> upcomingAssignments = assignmentService.getUpcomingAssignments();
        Integer upcomingCount = upcomingAssignments.size();

        String overallTrend = calculateOverallTrendFromCards(courseCards);

        int totalCompletedAssignments = 0;
        int totalGradedPoints = 0;
        int totalGradedPointsPossible = 0;
        int totalSemesterPoints = 0;
        int totalSemesterPointsPossible = 0;

        for (CourseCardData card : courseCards) {
            if (card.getCourseWithGrades() != null && card.getCourseWithGrades().getCourse() != null) {
                Integer courseId = Integer.parseInt(card.getCourseWithGrades().getCourse().getId());

                totalCompletedAssignments += gradeService.getCompletedAssignmentCount(courseId);

                PointsSummary gradedSummary = gradeService.calculateGradedPointsSummary(courseId);
                totalGradedPoints += gradedSummary.getPointsEarned();
                totalGradedPointsPossible += gradedSummary.getPointsTotal();

                PointsSummary semesterSummary = gradeService.calculatePointsSummary(courseId);
                totalSemesterPoints += semesterSummary.getPointsEarned();
                totalSemesterPointsPossible += semesterSummary.getPointsTotal();
            }
        }

        Double totalGradedPercentage = totalGradedPointsPossible > 0
            ? (totalGradedPoints * 100.0) / totalGradedPointsPossible : 0.0;

        SemesterSummary summary = new SemesterSummary(
                overallPercentage,
                totalCourses,
                upcomingCount,
                overallTrend
        );

        summary.setTotalCompletedAssignments(totalCompletedAssignments);
        summary.setTotalGradedPoints(totalGradedPoints);
        summary.setTotalGradedPointsPossible(totalGradedPointsPossible);
        summary.setTotalGradedPercentage(totalGradedPercentage);
        summary.setTotalSemesterPoints(totalSemesterPoints);
        summary.setTotalSemesterPointsPossible(totalSemesterPointsPossible);

        return summary;
    }

    private String calculateOverallTrendFromCards(List<CourseCardData> courseCards) {
        if (courseCards.isEmpty()) {
            return "stable";
        }

        int upCount = 0;
        int downCount = 0;

        for (CourseCardData card : courseCards) {
            String trend = card.getTrend();

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
