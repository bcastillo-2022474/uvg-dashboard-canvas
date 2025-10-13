package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.Submission;
import com.bestprograteam.canvas_dashboard.model.repositories.SubmissionRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock implementation of SubmissionRepository for testing.
 * Returns hardcoded submission data with grades.
 */
@Repository
public class MockSubmissionRepository implements SubmissionRepository {

    private final Map<Integer, List<Submission>> submissionsByCourse;

    public MockSubmissionRepository() {
        submissionsByCourse = new HashMap<>();

        // Course 101 submissions
        List<Submission> course101Submissions = new ArrayList<>();
        course101Submissions.add(createSubmission(3, 68.0, "graded", -5, false));  // Hash Table: 68/75
        course101Submissions.add(createSubmission(4, 142.0, "graded", -9, false)); // Graph Project: 142/150
        course101Submissions.add(createSubmission(5, 118.0, "graded", -16, false)); // Midterm: 118/125
        course101Submissions.add(createSubmission(1, null, "unsubmitted", null, null)); // Binary Tree (upcoming)
        course101Submissions.add(createSubmission(2, null, "unsubmitted", null, null)); // Sorting Lab (upcoming)
        submissionsByCourse.put(101, course101Submissions);

        // Course 102 submissions
        List<Submission> course102Submissions = new ArrayList<>();
        course102Submissions.add(createSubmission(8, 70.0, "graded", -7, false));  // Polymorphism: 70/75
        course102Submissions.add(createSubmission(9, 135.0, "graded", -12, false)); // Final Project Phase 1: 135/150
        course102Submissions.add(createSubmission(10, 48.0, "graded", -4, false)); // Unit Testing: 48/50
        course102Submissions.add(createSubmission(6, null, "unsubmitted", null, null)); // Design Patterns (upcoming)
        course102Submissions.add(createSubmission(7, null, "unsubmitted", null, null)); // Inheritance Lab (upcoming)
        submissionsByCourse.put(102, course102Submissions);

        // Course 103 submissions
        List<Submission> course103Submissions = new ArrayList<>();
        course103Submissions.add(createSubmission(13, 65.0, "graded", -10, false)); // Normalization: 65/75
        course103Submissions.add(createSubmission(14, 42.0, "graded", -6, false));  // Transaction Lab: 42/50
        course103Submissions.add(createSubmission(11, null, "unsubmitted", null, null)); // SQL Queries (upcoming)
        course103Submissions.add(createSubmission(12, null, "unsubmitted", null, null)); // Database Design (upcoming)
        submissionsByCourse.put(103, course103Submissions);

        // Course 104 submissions
        List<Submission> course104Submissions = new ArrayList<>();
        course104Submissions.add(createSubmission(16, 70.0, "graded", -8, false));  // UML Diagrams: 70/75
        course104Submissions.add(createSubmission(18, 46.0, "graded", -4, false));  // Code Review: 46/50
        course104Submissions.add(createSubmission(19, 185.0, "graded", -17, false)); // Final Project: 185/200
        course104Submissions.add(createSubmission(15, null, "unsubmitted", null, null)); // Requirements (upcoming)
        course104Submissions.add(createSubmission(17, null, "unsubmitted", null, null)); // Agile Sprint (upcoming)
        submissionsByCourse.put(104, course104Submissions);
    }

    private Submission createSubmission(Integer assignmentId, Double score, String state,
                                       Integer daysOffset, Boolean late) {
        Submission submission = new Submission();
        submission.assignmentId = assignmentId;
        submission.score = score;
        submission.workflowState = state;
        submission.late = late;

        if (daysOffset != null) {
            submission.gradedAt = LocalDateTime.now().plusDays(daysOffset);
        }

        return submission;
    }

    @Override
    public List<Submission> findSubmissionsByCourseId(Integer courseId) {
        return new ArrayList<>(submissionsByCourse.getOrDefault(courseId, new ArrayList<>()));
    }

    @Override
    public Submission findSubmissionByAssignmentId(Integer courseId, Integer assignmentId) {
        List<Submission> submissions = submissionsByCourse.get(courseId);
        if (submissions == null) {
            return null;
        }
        return submissions.stream()
                .filter(s -> s.assignmentId.equals(assignmentId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Submission> findRecentGrades(Integer courseId, Integer limit) {
        List<Submission> submissions = submissionsByCourse.get(courseId);
        if (submissions == null) {
            return new ArrayList<>();
        }

        return submissions.stream()
                .filter(s -> s.gradedAt != null)
                .sorted(Comparator.comparing((Submission s) -> s.gradedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
