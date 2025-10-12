# Services and Repositories Architecture

This document defines the service layer (business logic) and repository interfaces (ports) for the Canvas Dashboard application using Hexagonal Architecture principles.

##️ Architecture Overview

```
Controller Layer
     ↓ (uses)
Service Layer (Business Logic)
     ↓ (depends on)
Repository Interfaces (Ports)
     ↓ (implemented by)
Adapters (CanvasRepositoryImpl, MockRepositoryImpl)
```

**Key Principle**: Services depend on **interfaces** (ports), not concrete implementations. This allows us to swap implementations (Canvas API ↔ Mock data) without changing service code.

---

## Repository Interfaces (Ports)

These are the interfaces that services depend on. Each repository handles data access for one entity.

### **CourseRepository**
**Package**: `com.bestprograteam.canvas_dashboard.model.repositories`

```java
public interface CourseRepository {
    /**
     * Get all active courses for the current authenticated student.
     * @return List of courses
     */
    List<Course> findAllActiveCourses();

    /**
     * Get a specific course by ID.
     * @param courseId Canvas course ID
     * @return Course or null if not found
     */
    Course findCourseById(Integer courseId);
}
```

**Canvas API Endpoint**: `GET /api/v1/courses?enrollment_state=active`

---

### **EnrollmentRepository**
**Package**: `com.bestprograteam.canvas_dashboard.model.repositories`

```java
public interface EnrollmentRepository {
    /**
     * Get all active enrollments for the current student.
     * Includes Canvas-computed grades (current_score, final_score).
     * @return List of enrollments with grades
     */
    List<Enrollment> findAllEnrollments();

    /**
     * Get enrollment (with grades) for a specific course.
     * @param courseId Canvas course ID
     * @return Enrollment or null if not found
     */
    Enrollment findEnrollmentByCourseId(Integer courseId);
}
```

**Canvas API Endpoint**: `GET /api/v1/users/self/enrollments`

---

### **AssignmentRepository**
**Package**: `com.bestprograteam.canvas_dashboard.model.repositories`

```java
public interface AssignmentRepository {
    /**
     * Get all assignments for a course.
     * @param courseId Canvas course ID
     * @return List of assignments
     */
    List<Assignment> findAssignmentsByCourseId(Integer courseId);

    /**
     * Get a specific assignment by ID.
     * @param courseId Canvas course ID
     * @param assignmentId Canvas assignment ID
     * @return Assignment or null if not found
     */
    Assignment findAssignmentById(Integer courseId, Integer assignmentId);

    /**
     * Get assignments due within the next N days.
     * @param courseId Canvas course ID
     * @param days Number of days to look ahead
     * @return List of upcoming assignments
     */
    List<Assignment> findUpcomingAssignments(Integer courseId, Integer days);
}
```

**Canvas API Endpoint**: `GET /api/v1/courses/{course_id}/assignments`

---

### **SubmissionRepository**
**Package**: `com.bestprograteam.canvas_dashboard.model.repositories`

```java
public interface SubmissionRepository {
    /**
     * Get all submissions (including unsubmitted) for current student in a course.
     * @param courseId Canvas course ID
     * @return List of submissions
     */
    List<Submission> findSubmissionsByCourseId(Integer courseId);

    /**
     * Get a specific submission by assignment ID.
     * @param courseId Canvas course ID
     * @param assignmentId Canvas assignment ID
     * @return Submission or null if not found
     */
    Submission findSubmissionByAssignmentId(Integer courseId, Integer assignmentId);

    /**
     * Get the N most recent graded submissions for a course.
     * @param courseId Canvas course ID
     * @param limit Number of recent grades to return
     * @return List of recent submissions, sorted by graded_at DESC
     */
    List<Submission> findRecentGrades(Integer courseId, Integer limit);
}
```

**Canvas API Endpoint**: `GET /api/v1/courses/{course_id}/students/submissions`

---

### **AssignmentGroupRepository**
**Package**: `com.bestprograteam.canvas_dashboard.model.repositories`

```java
public interface AssignmentGroupRepository {
    /**
     * Get all assignment groups (grade categories) for a course.
     * @param courseId Canvas course ID
     * @return List of assignment groups with weights
     */
    List<AssignmentGroup> findAssignmentGroupsByCourseId(Integer courseId);

    /**
     * Get a specific assignment group by ID.
     * @param courseId Canvas course ID
     * @param groupId Canvas assignment group ID
     * @return AssignmentGroup or null if not found
     */
    AssignmentGroup findAssignmentGroupById(Integer courseId, Integer groupId);
}
```

**Canvas API Endpoint**: `GET /api/v1/courses/{course_id}/assignment_groups`

---

## 🎯 Service Layer (Business Logic)

Services orchestrate repositories and contain business logic for dashboard features.

### **CourseService**
**Package**: `com.bestprograteam.canvas_dashboard.service`

**Dependencies** (Constructor Injection):
- `CourseRepository`
- `EnrollmentRepository`

```java
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    // Constructor injection
    public CourseService(CourseRepository courseRepository,
                        EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Get all courses with their grades for dashboard display.
     * Combines course info with enrollment grades.
     * @return List of courses with grade information
     */
    public List<CourseWithGrades> getAllCoursesWithGrades() {
        // Logic: Fetch courses + enrollments, combine them
    }

    /**
     * Get a single course with its grade information.
     * @param courseId Canvas course ID
     * @return Course with grades or null
     */
    public CourseWithGrades getCourseWithGrades(Integer courseId) {
        // Logic: Fetch course + enrollment for specific course
    }

    /**
     * Calculate overall semester percentage across all courses.
     * @return Weighted average percentage
     */
    public Double calculateOverallPercentage() {
        // Logic: Get all enrollments, average currentScore
    }
}
```

---

### **AssignmentService**
**Package**: `com.bestprograteam.canvas_dashboard.service`

**Dependencies**:
- `AssignmentRepository`
- `SubmissionRepository`

```java
@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
                           SubmissionRepository submissionRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
    }

    /**
     * Get all assignments due within the next 7 days across all courses.
     * @return List of upcoming assignments
     */
    public List<Assignment> getUpcomingAssignments() {
        // Logic: Query all courses, filter by due date
    }

    /**
     * Get upcoming assignments for a specific course.
     * @param courseId Canvas course ID
     * @param days Number of days to look ahead
     * @return List of upcoming assignments
     */
    public List<Assignment> getUpcomingAssignmentsByCourse(Integer courseId, Integer days) {
        // Logic: Use assignmentRepository.findUpcomingAssignments()
    }

    /**
     * Count assignments by status for a course.
     * @param courseId Canvas course ID
     * @return Map of status -> count (e.g., "graded" -> 10, "unsubmitted" -> 2)
     */
    public Map<String, Integer> getAssignmentCountsByStatus(Integer courseId) {
        // Logic: Get submissions, group by workflowState, count
    }
}
```

---

### **GradeService**
**Package**: `com.bestprograteam.canvas_dashboard.service`

**Dependencies**:
- `SubmissionRepository`
- `AssignmentRepository`

```java
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
     * @param courseId Canvas course ID
     * @return List of recent submissions with assignment details
     */
    public List<RecentGrade> getRecentGrades(Integer courseId) {
        // Logic: Use submissionRepository.findRecentGrades(courseId, 5)
        // Combine with assignment details for display
    }

    /**
     * Calculate grade trend for a course (up/down/stable).
     * @param courseId Canvas course ID
     * @return Trend enum value
     */
    public String calculateGradeTrend(Integer courseId) {
        // Logic: Get recent submissions, compare percentages over time
    }

    /**
     * Calculate raw points summary for a course.
     * @param courseId Canvas course ID
     * @return PointsSummary object with earned/total/percentage
     */
    public PointsSummary calculatePointsSummary(Integer courseId) {
        // Logic: Sum assignment points and submission scores
    }
}
```

---

### **CategoryService**
**Package**: `com.bestprograteam.canvas_dashboard.service`

**Dependencies**:
- `AssignmentGroupRepository`
- `SubmissionRepository`
- `AssignmentRepository`

```java
@Service
public class CategoryService {

    private final AssignmentGroupRepository assignmentGroupRepository;
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;

    public CategoryService(AssignmentGroupRepository assignmentGroupRepository,
                          SubmissionRepository submissionRepository,
                          AssignmentRepository assignmentRepository) {
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Get grade breakdown by category for a course.
     * Calculates scores for each category (Exams 84%, Labs 89%, etc.)
     * @param courseId Canvas course ID
     * @return List of categories with computed scores
     */
    public List<CategoryBreakdown> getCategoryBreakdown(Integer courseId) {
        // Logic:
        // 1. Get assignment groups
        // 2. Get all submissions
        // 3. Get all assignments
        // 4. Group submissions by assignment_group_id
        // 5. Calculate percentage for each category
    }

    /**
     * Calculate weighted grade contribution per category.
     * @param courseId Canvas course ID
     * @return Map of category name -> weighted contribution to final grade
     */
    public Map<String, Double> getCategoryContributions(Integer courseId) {
        // Logic: Category score * group_weight for each category
    }
}
```

---

### **DashboardService**
**Package**: `com.bestprograteam.canvas_dashboard.service`

**Dependencies**:
- `CourseService`
- `AssignmentService`
- `GradeService`
- `CategoryService`

```java
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
        // Logic:
        // 1. Get all courses with grades
        // 2. Get upcoming assignments across all courses
        // 3. Calculate overall statistics
        // 4. Return aggregated DashboardData
    }

    /**
     * Get detailed data for a single course card.
     * @param courseId Canvas course ID
     * @return CourseCardData with all info for one course
     */
    public CourseCardData getCourseCardData(Integer courseId) {
        // Logic:
        // 1. Get course with grades
        // 2. Get recent grades for course
        // 3. Get category breakdown
        // 4. Get upcoming assignments for course
        // 5. Calculate trend
        // 6. Return aggregated CourseCardData
    }

    /**
     * Get semester summary statistics for top widgets.
     * @return SemesterSummary with overall stats
     */
    public SemesterSummary getSemesterSummary() {
        // Logic: Aggregate data from all courses for summary widgets
    }
}
```

---

## 📊 DTOs (Data Transfer Objects)

These are helper classes for service return types:

```java
// Course with grades combined
public class CourseWithGrades {
    private Course course;
    private Enrollment enrollment;
    // getters/setters
}

// Recent grade with assignment details
public class RecentGrade {
    private Submission submission;
    private Assignment assignment;
    // getters/setters
}

// Category breakdown with computed scores
public class CategoryBreakdown {
    private AssignmentGroup group;
    private Double currentScore;
    private Integer pointsEarned;
    private Integer pointsTotal;
    // getters/setters
}

// Complete dashboard data
public class DashboardData {
    private List<CourseWithGrades> courses;
    private List<Assignment> upcomingAssignments;
    private SemesterSummary summary;
    // getters/setters
}

// Course card data
public class CourseCardData {
    private CourseWithGrades courseWithGrades;
    private List<RecentGrade> recentGrades;
    private List<CategoryBreakdown> categoryBreakdown;
    private List<Assignment> upcomingAssignments;
    private String trend;
    // getters/setters
}
```

---

## 🔌 Implementation Strategy

### **Phase 1: Create Interfaces (Ports)**
1. Create all repository interfaces
2. Define method signatures
3. Document with JavaDoc

### **Phase 2: Create Mock Implementations**
1. Implement `MockCourseRepository implements CourseRepository`
2. Return hardcoded test data
3. Test services with mock data

### **Phase 3: Create Canvas Implementations**
1. Implement `CanvasCourseRepository implements CourseRepository`
2. Call Canvas API endpoints
3. Map JSON responses to entities

### **Phase 4: Implement Services**
1. Create service classes
2. Inject repository interfaces
3. Implement business logic

### **Phase 5: Wire Everything**
1. Configure Spring dependency injection
2. Test with mock repositories
3. Switch to Canvas repositories for production

---

## 🧪 Testing Strategy

**With Mock Repositories**:
```java
@Service
public class CourseService {
    public CourseService(CourseRepository courseRepository) {
        // In tests: inject MockCourseRepository
        // In production: inject CanvasCourseRepository
    }
}
```

This allows full service testing without hitting Canvas API!

---

## 📝 Summary

**5 Repository Interfaces** (Ports):
- CourseRepository
- EnrollmentRepository
- AssignmentRepository
- SubmissionRepository
- AssignmentGroupRepository

**5 Service Classes** (Business Logic):
- CourseService
- AssignmentService
- GradeService
- CategoryService
- DashboardService

**Implementation Flexibility**:
- Mock implementations for testing
- Canvas implementations for production
- Easy to swap without changing service code

This architecture keeps your code clean, testable, and maintainable! 🎉