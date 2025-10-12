package com.bestprograteam.canvas_dashboard.model.adapters;

import com.bestprograteam.canvas_dashboard.model.entities.Assignment;
import com.bestprograteam.canvas_dashboard.model.repositories.AssignmentRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mock implementation of AssignmentRepository for testing.
 * Returns hardcoded assignment data for multiple courses.
 */
@Repository
public class MockAssignmentRepository implements AssignmentRepository {

    private final Map<Integer, List<Assignment>> assignmentsByCourse;
    private int assignmentIdCounter = 1;

    public MockAssignmentRepository() {
        assignmentsByCourse = new HashMap<>();

        // Course 101 - Data Structures assignments
        List<Assignment> course101Assignments = new ArrayList<>();
        course101Assignments.add(createAssignment(101, "Binary Tree Implementation", 100.0, 1, "published", 2));
        course101Assignments.add(createAssignment(101, "Sorting Algorithms Lab", 50.0, 2, "published", 5));
        course101Assignments.add(createAssignment(101, "Hash Table Assignment", 75.0, 1, "published", -3));
        course101Assignments.add(createAssignment(101, "Graph Algorithms Project", 150.0, 1, "published", -7));
        course101Assignments.add(createAssignment(101, "Midterm Exam", 125.0, 3, "published", -14));
        assignmentsByCourse.put(101, course101Assignments);

        // Course 102 - OOP assignments
        List<Assignment> course102Assignments = new ArrayList<>();
        course102Assignments.add(createAssignment(102, "Design Patterns Assignment", 100.0, 1, "published", 1));
        course102Assignments.add(createAssignment(102, "Inheritance Lab", 50.0, 2, "published", 3));
        course102Assignments.add(createAssignment(102, "Polymorphism Exercise", 75.0, 1, "published", -5));
        course102Assignments.add(createAssignment(102, "Final Project Phase 1", 150.0, 1, "published", -10));
        course102Assignments.add(createAssignment(102, "Unit Testing Workshop", 50.0, 2, "published", -2));
        assignmentsByCourse.put(102, course102Assignments);

        // Course 103 - Database assignments
        List<Assignment> course103Assignments = new ArrayList<>();
        course103Assignments.add(createAssignment(103, "SQL Queries Assignment", 100.0, 1, "published", 4));
        course103Assignments.add(createAssignment(103, "Database Design Project", 150.0, 1, "published", 6));
        course103Assignments.add(createAssignment(103, "Normalization Exercise", 75.0, 1, "published", -8));
        course103Assignments.add(createAssignment(103, "Transaction Lab", 50.0, 2, "published", -4));
        assignmentsByCourse.put(103, course103Assignments);

        // Course 104 - Software Engineering assignments
        List<Assignment> course104Assignments = new ArrayList<>();
        course104Assignments.add(createAssignment(104, "Requirements Analysis", 100.0, 1, "published", 3));
        course104Assignments.add(createAssignment(104, "UML Diagrams Assignment", 75.0, 1, "published", -6));
        course104Assignments.add(createAssignment(104, "Agile Sprint Planning", 50.0, 2, "published", 1));
        course104Assignments.add(createAssignment(104, "Code Review Exercise", 50.0, 2, "published", -2));
        course104Assignments.add(createAssignment(104, "Final Project", 200.0, 1, "published", -15));
        assignmentsByCourse.put(104, course104Assignments);
    }

    private Assignment createAssignment(Integer courseId, String name, Double points,
                                       Integer groupId, String state, int daysOffset) {
        Assignment assignment = new Assignment();
        assignment.setId(assignmentIdCounter++);
        assignment.setCourseId(courseId);
        assignment.setName(name);
        assignment.setPointsPossible(points);
        assignment.setAssignmentGroupId(groupId);
        assignment.setWorkflowState(state);
        assignment.setDueAt(LocalDateTime.now().plusDays(daysOffset));
        return assignment;
    }

    @Override
    public List<Assignment> findAssignmentsByCourseId(Integer courseId) {
        return new ArrayList<>(assignmentsByCourse.getOrDefault(courseId, new ArrayList<>()));
    }

    @Override
    public Assignment findAssignmentById(Integer courseId, Integer assignmentId) {
        List<Assignment> assignments = assignmentsByCourse.get(courseId);
        if (assignments == null) {
            return null;
        }
        return assignments.stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Assignment> findUpcomingAssignments(Integer courseId, Integer days) {
        List<Assignment> assignments = assignmentsByCourse.get(courseId);
        if (assignments == null) {
            return new ArrayList<>();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);

        return assignments.stream()
                .filter(a -> a.getDueAt() != null)
                .filter(a -> a.getDueAt().isAfter(now) && a.getDueAt().isBefore(future))
                .collect(Collectors.toList());
    }
}
