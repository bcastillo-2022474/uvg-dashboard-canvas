# Team Task Distribution - Canvas Dashboard Entities

## 📋 Overview

This document outlines the individual tasks for implementing the Canvas Dashboard entity layer. Each team member will create one entity class following our [entity definitions](./entity-definitions.md).

**Team Size**: 5 developers  
**Requirement**: 3 commits per person  
**Difficulty**: ⭐ Beginner-friendly (15-25 lines per class)  
**No API integration required** - just Java entity classes

---

## 👥 Task Assignments

### **Person 1: Course Entity**
**File**: `src/main/java/com/bestprograteam/canvas_dashboard/model/entities/Course.java`

**Description**: Create the core Course entity that represents a Canvas course for dashboard display.

**Fields to implement**:
```java
private Integer id;                    // Canvas course ID
private String name;                   // Full course name
private String courseCode;             // Extracted code (e.g., "CC2008")
private String displayName;            // Shortened UI name
private String colorCode;              // Hex color for visual distinction
private String workflowState;          // Course availability status
private Integer completedAssignments; // Count of graded submissions
private Integer totalAssignments;     // Count of all assignments
private Integer upcomingAssignments;  // Count due within 7 days
```

**Commits Required**:
1. **Initial entity**: Create Course.java with basic fields (id, name, courseCode, workflowState)
2. **Display fields**: Add displayName, colorCode and computed assignment counts
3. **Complete class**: Add all getters/setters, toString, and JavaDoc comments

---

### **Person 2: Enrollment Entity**
**File**: `src/main/java/com/bestprograteam/canvas_dashboard/model/entities/Enrollment.java`

**Description**: Create the Enrollment entity that holds Canvas-computed course grades.

**Fields to implement**:
```java
private Integer courseId;              // Reference to Course
private String enrollmentState;        // Student enrollment status
private Double currentScore;           // Canvas-computed grade percentage (graded only)
private Double finalScore;             // Canvas-computed semester percentage (with zeros)
private String currentGrade;           // Letter grade (if available)
private String finalGrade;             // Final letter grade (if available)
private LocalDateTime lastActivityAt;   // Last activity timestamp
private Integer totalActivityTime;     // Total seconds in course
```

**Commits Required**:
1. **Core grades**: Create Enrollment.java with courseId and score fields (currentScore, finalScore)
2. **Grade details**: Add currentGrade, finalGrade, and enrollmentState
3. **Activity tracking**: Add lastActivityAt, totalActivityTime, getters/setters, and JavaDoc

---

### **Person 3: Assignment Entity**
**File**: `src/main/java/com/bestprograteam/canvas_dashboard/model/entities/Assignment.java`

**Description**: Create the Assignment entity for due date tracking and points calculation.

**Fields to implement**:
```java
private Integer id;                    // Canvas assignment ID
private Integer courseId;              // Reference to Course
private String name;                   // Assignment title
private LocalDateTime dueAt;           // Due date for "due soon" calculations
private Double pointsPossible;         // Points possible for semester totals
private Integer assignmentGroupId;     // Reference to AssignmentGroup
private String workflowState;          // Published status filter
```

**Commits Required**:
1. **Basic assignment**: Create Assignment.java with id, courseId, name, and dueAt
2. **Points and grouping**: Add pointsPossible, assignmentGroupId, workflowState
3. **Helper methods**: Add getters/setters, isDueSoon() helper method, and JavaDoc

---

### **Person 4: Submission Entity**
**File**: `src/main/java/com/bestprograteam/canvas_dashboard/model/entities/Submission.java`

**Description**: Create the Submission entity for student submission scores and grade calculations.

**Fields to implement**:
```java
private Integer assignmentId;          // Reference to Assignment
private Double score;                  // Points earned (null if unsubmitted)
private LocalDateTime gradedAt;        // Grading timestamp for trends/recent grades
private String workflowState;          // Submission status filter
private Boolean late;                  // Late submission indicator
```

**Commits Required**:
1. **Core submission**: Create Submission.java with assignmentId, score, and workflowState
2. **Status tracking**: Add gradedAt, late fields for status indicators
3. **Helper methods**: Add getters/setters, isGraded(), isLate() helper methods, and JavaDoc

---

### **Person 5: AssignmentGroup Entity**
**File**: `src/main/java/com/bestprograteam/canvas_dashboard/model/entities/AssignmentGroup.java`

**Description**: Create the AssignmentGroup entity for grade category weights and computed scores.

**Fields to implement**:
```java
private Integer id;                    // Canvas assignment group ID
private Integer courseId;              // Reference to Course
private String name;                   // Category name (e.g., "Exams", "Labs")
private Integer position;              // Display order
private Double groupWeight;            // Percentage of final grade
private Double currentScore;           // Computed average for this category
private Double pointsEarned;           // Sum of scores in category
private Double pointsTotal;            // Sum of points possible in category
```

**Commits Required**:
1. **Basic group**: Create AssignmentGroup.java with id, courseId, name, position
2. **Weighting**: Add groupWeight field and basic computed score fields
3. **Complete calculations**: Add pointsEarned, pointsTotal, getters/setters, and JavaDoc

---

## 📁 File Structure

After completion, your entity structure should look like:
```
src/main/java/com/bestprograteam/canvas_dashboard/
└── model/
    └── entities/
        ├── Course.java
        ├── Enrollment.java
        ├── Assignment.java
        ├── Submission.java
        └── AssignmentGroup.java
```

---

## 🔧 Implementation Guidelines

### **Java Standards**:
- Use `Integer` for IDs (nullable)
- Use `Double` for scores/percentages (nullable)
- Use `LocalDateTime` for timestamps
- Use `Boolean` for flags (nullable)
- Use `String` for text fields

### **Required Methods**:
- **Getters and Setters** for all fields
- **toString()** method for debugging
- **JavaDoc comments** explaining the entity purpose

### **Example Entity Template**:
```java
package com.bestprograteam.canvas_dashboard.model.entities;

import java.time.LocalDateTime;

/**
 * Represents a [Entity Name] in the Canvas Dashboard.
 * Used for [brief description of purpose].
 */
public class YourEntity {
    private Integer id;
    private String name;
    
    // Constructors
    public YourEntity() {}
    
    public YourEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    // Helper methods (if applicable)
    public boolean isActive() {
        return "active".equals(status);
    }
    
    @Override
    public String toString() {
        return "YourEntity{id=" + id + ", name='" + name + "'}";
    }
}
```

---

## 📝 Commit Message Guidelines

**Format**: `type: brief description`

**Examples**:
- `feat: create Course entity with basic fields`
- `feat: add display and computed fields to Course`
- `docs: add getters/setters and JavaDoc to Course`

---

## ✅ Completion Checklist

Before marking your task complete, ensure:

- [ ] Entity class created in correct package
- [ ] All required fields implemented
- [ ] All getters and setters added
- [ ] toString() method implemented
- [ ] JavaDoc comments added
- [ ] 3 commits pushed with clear messages
- [ ] Code follows Java naming conventions
- [ ] No compilation errors

---