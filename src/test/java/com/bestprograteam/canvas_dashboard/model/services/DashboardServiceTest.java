package com.bestprograteam.canvas_dashboard.model.services;

import com.bestprograteam.canvas_dashboard.model.dto.*;
import com.bestprograteam.canvas_dashboard.model.entities.*;
import com.bestprograteam.canvas_dashboard.model.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private AssignmentGroupRepository assignmentGroupRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(
                courseRepository,
                enrollmentRepository,
                assignmentRepository,
                submissionRepository,
                assignmentGroupRepository
        );
    }

    @Test
    void shouldFetchDashboardDataSuccessfully() {
        // Arrange
        Course course = createCourse("101", "Data Structures", "CS2021");
        Enrollment enrollment = createEnrollment(101, 85.0, "B");
        List<Assignment> assignments = createAssignments(101);
        List<Submission> submissions = createSubmissions();
        List<AssignmentGroup> groups = createAssignmentGroups(101);

        when(courseRepository.findAllActiveCourses()).thenReturn(List.of(course));
        when(enrollmentRepository.findAllEnrollments()).thenReturn(List.of(enrollment));
        when(assignmentRepository.findAssignmentsByCourseId(101)).thenReturn(assignments);
        when(submissionRepository.findSubmissionsByCourseId(101)).thenReturn(submissions);
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(101)).thenReturn(groups);

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.courseCards).hasSize(1);
        assertThat(result.courseCards.get(0).courseWithGrades.course.name).isEqualTo("Data Structures");
        assertThat(result.courseCards.get(0).courseWithGrades.enrollment.currentScore).isEqualTo(85.0);
        assertThat(result.summary).isNotNull();
        assertThat(result.summary.totalCourses).isEqualTo(1);
    }

    @Test
    void shouldHandleEmptyCourseList() {
        // Arrange
        when(courseRepository.findAllActiveCourses()).thenReturn(Collections.emptyList());
        when(enrollmentRepository.findAllEnrollments()).thenReturn(Collections.emptyList());

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.courseCards).isEmpty();
        assertThat(result.upcomingAssignments).isEmpty();
        assertThat(result.summary.totalCourses).isEqualTo(0);
        assertThat(result.summary.overallPercentage).isEqualTo(0.0);
    }

    @Test
    void shouldCalculateRecentGradesCorrectly() {
        // Arrange
        Course course = createCourse("101", "Data Structures", "CS2021");
        Enrollment enrollment = createEnrollment(101, 85.0, "B");

        List<Assignment> assignments = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            assignments.add(createAssignment(i, 101, "Assignment " + i, 100.0, 1));
        }

        List<Submission> submissions = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            submissions.add(createSubmission(i, 85.0, LocalDateTime.now().minusDays(10 - i)));
        }

        List<AssignmentGroup> groups = createAssignmentGroups(101);

        when(courseRepository.findAllActiveCourses()).thenReturn(List.of(course));
        when(enrollmentRepository.findAllEnrollments()).thenReturn(List.of(enrollment));
        when(assignmentRepository.findAssignmentsByCourseId(101)).thenReturn(assignments);
        when(submissionRepository.findSubmissionsByCourseId(101)).thenReturn(submissions);
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(101)).thenReturn(groups);

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        assertThat(result.courseCards).hasSize(1);
        CourseCardData cardData = result.courseCards.get(0);
        assertThat(cardData.recentGrades).hasSize(5); // Limited to 5

        // Verify sorted by gradedAt DESC (most recent first)
        LocalDateTime previousDate = LocalDateTime.MAX;
        for (RecentGrade grade : cardData.recentGrades) {
            assertThat(grade.submission.gradedAt).isBeforeOrEqualTo(previousDate);
            previousDate = grade.submission.gradedAt;
        }
    }

    @Test
    void shouldCalculateCategoryBreakdownCorrectly() {
        // Arrange
        Course course = createCourse("101", "Data Structures", "CS2021");
        Enrollment enrollment = createEnrollment(101, 85.0, "B");

        List<Assignment> assignments = List.of(
                createAssignment(1, 101, "Assignment 1", 100.0, 1),
                createAssignment(2, 101, "Assignment 2", 100.0, 1),
                createAssignment(3, 101, "Lab 1", 50.0, 2)
        );

        List<Submission> submissions = List.of(
                createSubmission(1, 85.0, LocalDateTime.now().minusDays(3)),
                createSubmission(2, 90.0, LocalDateTime.now().minusDays(2)),
                createSubmission(3, 40.0, LocalDateTime.now().minusDays(1))
        );

        List<AssignmentGroup> groups = List.of(
                createAssignmentGroup(1, 101, "Assignments", 50.0),
                createAssignmentGroup(2, 101, "Labs", 20.0)
        );

        when(courseRepository.findAllActiveCourses()).thenReturn(List.of(course));
        when(enrollmentRepository.findAllEnrollments()).thenReturn(List.of(enrollment));
        when(assignmentRepository.findAssignmentsByCourseId(101)).thenReturn(assignments);
        when(submissionRepository.findSubmissionsByCourseId(101)).thenReturn(submissions);
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(101)).thenReturn(groups);

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        CourseCardData cardData = result.courseCards.get(0);
        assertThat(cardData.categoryBreakdown).hasSize(2);

        CategoryBreakdown assignmentsBreakdown = cardData.categoryBreakdown.stream()
                .filter(cb -> cb.group.name.equals("Assignments"))
                .findFirst()
                .orElseThrow();

        assertThat(assignmentsBreakdown.pointsEarned).isEqualTo(175); // 85 + 90
        assertThat(assignmentsBreakdown.pointsTotal).isEqualTo(200); // 100 + 100
        assertThat(assignmentsBreakdown.currentScore).isEqualTo(87.5); // (175/200) * 100
        assertThat(assignmentsBreakdown.completedAssignments).isEqualTo(2);
        assertThat(assignmentsBreakdown.totalAssignments).isEqualTo(2);

        CategoryBreakdown labsBreakdown = cardData.categoryBreakdown.stream()
                .filter(cb -> cb.group.name.equals("Labs"))
                .findFirst()
                .orElseThrow();

        assertThat(labsBreakdown.pointsEarned).isEqualTo(40);
        assertThat(labsBreakdown.pointsTotal).isEqualTo(50);
        assertThat(labsBreakdown.currentScore).isEqualTo(80.0); // (40/50) * 100
    }

    @Test
    void shouldCalculateUpcomingAssignments() {
        // Arrange
        Course course = createCourse("101", "Data Structures", "CS2021");
        Enrollment enrollment = createEnrollment(101, 85.0, "B");

        LocalDateTime now = LocalDateTime.now();
        List<Assignment> assignments = List.of(
                createAssignmentWithDueDate(1, 101, "Due in 2 days", 100.0, 1, now.plusDays(2)),
                createAssignmentWithDueDate(2, 101, "Due in 5 days", 100.0, 1, now.plusDays(5)),
                createAssignmentWithDueDate(3, 101, "Due in 10 days", 100.0, 1, now.plusDays(10)), // Outside 7 day window
                createAssignmentWithDueDate(4, 101, "Past due", 100.0, 1, now.minusDays(1)) // Past due
        );

        List<Submission> submissions = Collections.emptyList();
        List<AssignmentGroup> groups = createAssignmentGroups(101);

        when(courseRepository.findAllActiveCourses()).thenReturn(List.of(course));
        when(enrollmentRepository.findAllEnrollments()).thenReturn(List.of(enrollment));
        when(assignmentRepository.findAssignmentsByCourseId(101)).thenReturn(assignments);
        when(submissionRepository.findSubmissionsByCourseId(101)).thenReturn(submissions);
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(101)).thenReturn(groups);

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        CourseCardData cardData = result.courseCards.get(0);
        assertThat(cardData.upcomingAssignments).hasSize(2); // Only assignments within 7 days
        assertThat(cardData.upcomingAssignments)
                .extracting(a -> a.name)
                .containsExactlyInAnyOrder("Due in 2 days", "Due in 5 days");
    }

    @Test
    void shouldCalculateSemesterSummary() {
        // Arrange
        List<Course> courses = List.of(
                createCourse("101", "Data Structures", "CS2021"),
                createCourse("102", "OOP", "CC2008")
        );
        List<Enrollment> enrollments = List.of(
                createEnrollment(101, 85.0, "B"),
                createEnrollment(102, 90.0, "A")
        );

        when(courseRepository.findAllActiveCourses()).thenReturn(courses);
        when(enrollmentRepository.findAllEnrollments()).thenReturn(enrollments);

        // Mock repositories to return empty data for simplicity
        when(assignmentRepository.findAssignmentsByCourseId(any())).thenReturn(Collections.emptyList());
        when(submissionRepository.findSubmissionsByCourseId(any())).thenReturn(Collections.emptyList());
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(any())).thenReturn(Collections.emptyList());

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        SemesterSummary summary = result.summary;
        assertThat(summary.totalCourses).isEqualTo(2);
        assertThat(summary.overallPercentage).isEqualTo(87.5); // (85 + 90) / 2
    }

    @Test
    void shouldCalculateTrendUp() {
        // Arrange
        Course course = createCourse("101", "Data Structures", "CS2021");
        Enrollment enrollment = createEnrollment(101, 85.0, "B");

        List<Assignment> assignments = List.of(
                createAssignment(1, 101, "Assignment 1", 100.0, 1),
                createAssignment(2, 101, "Assignment 2", 100.0, 1),
                createAssignment(3, 101, "Assignment 3", 100.0, 1),
                createAssignment(4, 101, "Assignment 4", 100.0, 1)
        );

        // Submissions: improving trend (70 -> 75 -> 85 -> 90)
        List<Submission> submissions = List.of(
                createSubmission(1, 70.0, LocalDateTime.now().minusDays(4)),
                createSubmission(2, 75.0, LocalDateTime.now().minusDays(3)),
                createSubmission(3, 85.0, LocalDateTime.now().minusDays(2)),
                createSubmission(4, 90.0, LocalDateTime.now().minusDays(1))
        );

        List<AssignmentGroup> groups = createAssignmentGroups(101);

        when(courseRepository.findAllActiveCourses()).thenReturn(List.of(course));
        when(enrollmentRepository.findAllEnrollments()).thenReturn(List.of(enrollment));
        when(assignmentRepository.findAssignmentsByCourseId(101)).thenReturn(assignments);
        when(submissionRepository.findSubmissionsByCourseId(101)).thenReturn(submissions);
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(101)).thenReturn(groups);

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        CourseCardData cardData = result.courseCards.get(0);
        assertThat(cardData.trend).isEqualTo("up");
    }

    @Test
    void shouldCalculateTrendDown() {
        // Arrange
        Course course = createCourse("101", "Data Structures", "CS2021");
        Enrollment enrollment = createEnrollment(101, 85.0, "B");

        List<Assignment> assignments = List.of(
                createAssignment(1, 101, "Assignment 1", 100.0, 1),
                createAssignment(2, 101, "Assignment 2", 100.0, 1),
                createAssignment(3, 101, "Assignment 3", 100.0, 1),
                createAssignment(4, 101, "Assignment 4", 100.0, 1)
        );

        // Submissions: declining trend (90 -> 85 -> 75 -> 70)
        List<Submission> submissions = List.of(
                createSubmission(1, 90.0, LocalDateTime.now().minusDays(4)),
                createSubmission(2, 85.0, LocalDateTime.now().minusDays(3)),
                createSubmission(3, 75.0, LocalDateTime.now().minusDays(2)),
                createSubmission(4, 70.0, LocalDateTime.now().minusDays(1))
        );

        List<AssignmentGroup> groups = createAssignmentGroups(101);

        when(courseRepository.findAllActiveCourses()).thenReturn(List.of(course));
        when(enrollmentRepository.findAllEnrollments()).thenReturn(List.of(enrollment));
        when(assignmentRepository.findAssignmentsByCourseId(101)).thenReturn(assignments);
        when(submissionRepository.findSubmissionsByCourseId(101)).thenReturn(submissions);
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(101)).thenReturn(groups);

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        CourseCardData cardData = result.courseCards.get(0);
        assertThat(cardData.trend).isEqualTo("down");
    }

    @Test
    void shouldCalculateTrendStable() {
        // Arrange
        Course course = createCourse("101", "Data Structures", "CS2021");
        Enrollment enrollment = createEnrollment(101, 85.0, "B");

        List<Assignment> assignments = List.of(
                createAssignment(1, 101, "Assignment 1", 100.0, 1),
                createAssignment(2, 101, "Assignment 2", 100.0, 1),
                createAssignment(3, 101, "Assignment 3", 100.0, 1),
                createAssignment(4, 101, "Assignment 4", 100.0, 1)
        );

        // Submissions: stable trend (all around 85)
        List<Submission> submissions = List.of(
                createSubmission(1, 84.0, LocalDateTime.now().minusDays(4)),
                createSubmission(2, 85.0, LocalDateTime.now().minusDays(3)),
                createSubmission(3, 86.0, LocalDateTime.now().minusDays(2)),
                createSubmission(4, 85.0, LocalDateTime.now().minusDays(1))
        );

        List<AssignmentGroup> groups = createAssignmentGroups(101);

        when(courseRepository.findAllActiveCourses()).thenReturn(List.of(course));
        when(enrollmentRepository.findAllEnrollments()).thenReturn(List.of(enrollment));
        when(assignmentRepository.findAssignmentsByCourseId(101)).thenReturn(assignments);
        when(submissionRepository.findSubmissionsByCourseId(101)).thenReturn(submissions);
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(101)).thenReturn(groups);

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        CourseCardData cardData = result.courseCards.get(0);
        assertThat(cardData.trend).isEqualTo("stable");
    }

    @Test
    void shouldHandleCoursesWithNoAssignments() {
        // Arrange
        Course course = createCourse("101", "Data Structures", "CS2021");
        Enrollment enrollment = createEnrollment(101, 85.0, "B");

        when(courseRepository.findAllActiveCourses()).thenReturn(List.of(course));
        when(enrollmentRepository.findAllEnrollments()).thenReturn(List.of(enrollment));
        when(assignmentRepository.findAssignmentsByCourseId(101)).thenReturn(Collections.emptyList());
        when(submissionRepository.findSubmissionsByCourseId(101)).thenReturn(Collections.emptyList());
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(101)).thenReturn(Collections.emptyList());

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        assertThat(result.courseCards).hasSize(1);
        CourseCardData cardData = result.courseCards.get(0);
        assertThat(cardData.upcomingAssignments).isEmpty();
        assertThat(cardData.recentGrades).isEmpty();
        assertThat(cardData.categoryBreakdown).isEmpty();
        assertThat(cardData.remainingPoints).isEqualTo(0.0);
    }

    @Test
    void shouldHandleCoursesWithNoSubmissions() {
        // Arrange
        Course course = createCourse("101", "Data Structures", "CS2021");
        Enrollment enrollment = createEnrollment(101, 85.0, "B");
        List<Assignment> assignments = createAssignments(101);

        when(courseRepository.findAllActiveCourses()).thenReturn(List.of(course));
        when(enrollmentRepository.findAllEnrollments()).thenReturn(List.of(enrollment));
        when(assignmentRepository.findAssignmentsByCourseId(101)).thenReturn(assignments);
        when(submissionRepository.findSubmissionsByCourseId(101)).thenReturn(Collections.emptyList());
        when(assignmentGroupRepository.findAssignmentGroupsByCourseId(101)).thenReturn(createAssignmentGroups(101));

        // Act
        DashboardData result = dashboardService.getDashboardData();

        // Assert
        assertThat(result.courseCards).hasSize(1);
        CourseCardData cardData = result.courseCards.get(0);
        assertThat(cardData.recentGrades).isEmpty();
        assertThat(cardData.trend).isEqualTo("stable"); // Less than 2 grades

        // Category breakdown should exist but with zero points
        for (CategoryBreakdown breakdown : cardData.categoryBreakdown) {
            assertThat(breakdown.pointsEarned).isEqualTo(0);
            assertThat(breakdown.completedAssignments).isEqualTo(0);
        }
    }

    // Helper methods to create test data

    private Course createCourse(String id, String name, String code) {
        Course course = new Course();
        course.id = id;
        course.name = name;
        course.code = code;
        course.status = "active";
        course.enrollmentType = "StudentEnrollment";
        course.startDate = new Date();
        course.endDate = new Date();
        course.totalPoints = 500.0f;
        course.currentGrade = 85.0f;
        return course;
    }

    private Enrollment createEnrollment(Integer courseId, Double currentScore, String currentGrade) {
        Enrollment enrollment = new Enrollment();
        enrollment.courseId = courseId;
        enrollment.enrollmentState = "active";
        enrollment.currentScore = currentScore;
        enrollment.finalScore = currentScore;
        enrollment.currentGrade = currentGrade;
        enrollment.lastActivityAt = LocalDateTime.now();
        enrollment.totalActivityTime = 14400;
        return enrollment;
    }

    private Assignment createAssignment(Integer id, Integer courseId, String name, Double points, Integer groupId) {
        Assignment assignment = new Assignment();
        assignment.id = id;
        assignment.courseId = courseId;
        assignment.name = name;
        assignment.pointsPossible = points;
        assignment.assignmentGroupId = groupId;
        assignment.workflowState = "published";
        assignment.dueAt = LocalDateTime.now().plusDays(3);
        return assignment;
    }

    private Assignment createAssignmentWithDueDate(Integer id, Integer courseId, String name, Double points, Integer groupId, LocalDateTime dueAt) {
        Assignment assignment = createAssignment(id, courseId, name, points, groupId);
        assignment.dueAt = dueAt;
        return assignment;
    }

    private Submission createSubmission(Integer assignmentId, Double score, LocalDateTime gradedAt) {
        Submission submission = new Submission();
        submission.assignmentId = assignmentId;
        submission.score = score;
        submission.workflowState = "graded";
        submission.gradedAt = gradedAt;
        submission.late = false;
        return submission;
    }

    private AssignmentGroup createAssignmentGroup(Integer id, Integer courseId, String name, Double weight) {
        AssignmentGroup group = new AssignmentGroup();
        group.id = id;
        group.courseId = courseId;
        group.name = name;
        group.position = 1;
        group.groupWeight = weight;
        return group;
    }

    private List<Assignment> createAssignments(Integer courseId) {
        return List.of(
                createAssignment(1, courseId, "Binary Tree Implementation", 100.0, 1),
                createAssignment(2, courseId, "Sorting Algorithms Lab", 50.0, 2),
                createAssignment(3, courseId, "Hash Table Assignment", 75.0, 1)
        );
    }

    private List<Submission> createSubmissions() {
        return List.of(
                createSubmission(1, 85.0, LocalDateTime.now().minusDays(3)),
                createSubmission(2, 42.0, LocalDateTime.now().minusDays(2)),
                createSubmission(3, 68.0, LocalDateTime.now().minusDays(1))
        );
    }

    private List<AssignmentGroup> createAssignmentGroups(Integer courseId) {
        return List.of(
                createAssignmentGroup(1, courseId, "Assignments", 50.0),
                createAssignmentGroup(2, courseId, "Labs", 20.0),
                createAssignmentGroup(3, courseId, "Exams", 30.0)
        );
    }
}
