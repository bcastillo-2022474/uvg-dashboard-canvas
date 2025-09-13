# Entity Definitions - Canvas Dashboard

This document defines the entities needed for the Canvas Dashboard application. Each entity focuses only on fields required for dashboard calculations and display.

## Course Entity

**Purpose**: Core course information for display
**Dashboard Usage**: Course cards, course identification

```yaml
Course:
  # Core identification (from Canvas API)
  id: integer                    # canvas: id - course identifier
  name: string                   # canvas: name - full course name
  courseCode: string             # derived: extracted from course_code ("CC2008")
  displayName: string            # derived: shortened for UI ("Programación OOP")
  colorCode: string              # generated: visual distinction
  
  # Academic status (from Canvas API) 
  workflowState: string          # canvas: workflow_state - course availability
  
  # Dashboard computed metrics (calculated from other entities)
  trend: enum[up, down, stable]  # calculated: from recent submission scores
  completedAssignments: integer  # calculated: count of graded submissions
  totalAssignments: integer      # calculated: count of all assignments
  upcomingAssignments: integer   # calculated: count due within 7 days
```

## Enrollment Entity

**Purpose**: Canvas-computed course grades (eliminates manual calculation!)
**Dashboard Usage**: Course percentage, letter grade, semester vs graded points distinction

```yaml
Enrollment:
  # Core identification (from Canvas API)
  courseId: integer              # canvas: course_id - links to Course
  enrollmentState: string        # canvas: enrollment_state - student status
  
  # Canvas-computed grades (from Canvas API)
  currentScore: decimal          # canvas: current_score - percentage of GRADED work only
  finalScore: decimal            # canvas: final_score - percentage of ALL work (unsubmitted = 0)
  currentGrade: string           # canvas: current_grade - letter grade (if available)
  finalGrade: string             # canvas: final_grade - final letter grade (if available)
  
  # Activity tracking (from Canvas API)
  lastActivityAt: datetime       # canvas: last_activity_at - for engagement tracking
  totalActivityTime: integer     # canvas: total_activity_time - seconds spent in course
```

## Assignment Entity

**Purpose**: Assignment data for due date tracking and points calculation
**Dashboard Usage**: "Due Soon" sections, points totals, assignment counting

```yaml
Assignment:
  # Core identification (from Canvas API)
  id: integer                   # canvas: id - assignment identifier
  courseId: integer            # canvas: course_id - course reference
  name: string                 # canvas: name - assignment title
  
  # Essential timing (from Canvas API)
  dueAt: datetime              # canvas: due_at - for "due soon" calculations
  
  # Points and grading (from Canvas API)
  pointsPossible: decimal      # canvas: points_possible - for semester totals
  assignmentGroupId: integer   # canvas: assignment_group_id - for category breakdown
  
  # Status filtering (from Canvas API)
  workflowState: string        # canvas: workflow_state - filter published only
```

## Submission Entity

**Purpose**: Student submission scores for grade calculations
**Dashboard Usage**: Recent grades, grade trends, points earned calculations

```yaml
Submission:
  # Core identification (from Canvas API)
  assignmentId: integer        # canvas: assignment_id - links to Assignment
  
  # Score data (from Canvas API)
  score: decimal               # canvas: score - points earned (null if unsubmitted)
  gradedAt: datetime           # canvas: graded_at - for trend analysis & recent grades
  
  # Status (from Canvas API)
  workflowState: string        # canvas: workflow_state - filter graded submissions
  late: boolean                # canvas: late - for status indicators
```

## AssignmentGroup Entity

**Purpose**: Grade category weights and computed scores for category breakdown
**Dashboard Usage**: Grade category breakdown sections ("Exams 84%, Labs 89%")

```yaml
AssignmentGroup:
  # Core identification (from Canvas API)
  id: integer                  # canvas: id - group identifier
  courseId: integer           # derived: course reference
  name: string                # canvas: name - category name ("Actividades formativas")
  position: integer           # canvas: position - display order
  
  # Weighting (from Canvas API)
  groupWeight: decimal        # canvas: group_weight - percentage of final grade (12.0)
  
  # Computed category scores (calculated from submissions)
  currentScore: decimal       # calculated: average percentage for this category
  pointsEarned: decimal       # calculated: sum of scores in this category
  pointsTotal: decimal        # calculated: sum of points_possible in this category
  completedAssignments: integer # calculated: count of graded submissions in category
  totalAssignments: integer   # calculated: count of assignments in category
```

## Calculation Dependencies

### Dashboard Data Sources

| Dashboard Display | Data Source | Calculation |
|------------------|-------------|-------------|
| **Course Percentage** | `enrollment.currentScore` | ✅ **Canvas computed** - no calculation needed |
| **Letter Grade** | `enrollment.currentGrade` or derived from `currentScore` | ✅ **Canvas computed** or simple lookup |
| **Semester vs Graded Points** | `enrollment.finalScore` vs `enrollment.currentScore` | ✅ **Canvas computed** - no calculation needed |
| **Recent Grades** | `submissions` sorted by `gradedAt` | Filter and sort submissions |
| **Due Soon** | `assignments` filtered by `dueAt` | Count assignments due within 7 days |
| **Trend** | Recent submission percentages over time | Calculate from submission history |
| **Category Breakdown** | `assignmentGroups` + `submissions` | Group submissions by assignment group |
| **Raw Points Totals** | Sum from `assignments.pointsPossible` + `submissions.score` | Calculate: semester total, graded total |
| **Assignment Counts** | Count from `submissions` by `workflowState` | Calculate: completed, total, upcoming |

## API Mapping Strategy

| Entity | Primary Canvas API | Purpose |
|--------|-------------------|---------|
| Course | `/api/v1/courses` | Basic course info |
| **Enrollment** | `/api/v1/users/self/enrollments` | **Canvas-computed grades** (most important!) |
| Assignment | `/api/v1/courses/{id}/assignments` | Assignment details and due dates |
| Submission | `/api/v1/courses/{id}/students/submissions` | Recent grades and trend analysis |
| AssignmentGroup | `/api/v1/courses/{id}/assignment_groups` | Grade category weights |

### Essential API Calls
```
# 🎯 MOST IMPORTANT: Get ALL course grades in ONE call
GET /api/v1/users/self/enrollments

# Get basic course info (for display names, course codes)  
GET /api/v1/courses?enrollment_state=active

# Get assignments for due dates and points
GET /api/v1/courses/{course_id}/assignments?per_page=100

# Get submissions for recent grades and trend analysis
GET /api/v1/courses/{course_id}/students/submissions

# Get assignment groups for category breakdown
GET /api/v1/courses/{course_id}/assignment_groups
```

### Key API Insights

**Enrollments API** (`/api/v1/users/self/enrollments`):
- ✅ **Single call gets ALL course grades** - most efficient
- ✅ **Canvas does the math** - `current_score` and `final_score` are computed 
- ✅ **Handles weighted grading** - no need to manually calculate assignment group weights
- ✅ **Semester vs graded distinction** - `final_score` includes zeros, `current_score` doesn't
- ✅ **Returns active enrollments only** - no historical data, perfect for current semester

**Submissions API** (`/api/v1/courses/{id}/students/submissions`):
- ✅ **Returns ALL submissions** (graded, submitted, unsubmitted) 
- ✅ **Perfect for recent grades** - sort by `graded_at`
- ✅ **Trend analysis** - historical submission percentages

**Assignment Groups API** (`/api/v1/courses/{id}/assignment_groups`):
- ✅ **Category names and weights** - "Actividades formativas" (12.0%)
- ✅ **Display positioning** - `position` field for UI ordering
- ❌ **No computed category scores** - must calculate from submissions grouped by `assignment_group_id`

### ❌ Known Data Limitations

**Class Averages**: 
- Canvas API **does not expose class statistics** to students
- Admin-only endpoints exist but are inaccessible 
- **Dashboard solution**: Either remove class average display or use placeholder/mock data

**Historical Enrollments**:
- Enrollments API already filters to active enrollments
- No additional filtering needed for current semester

## Implementation Priority

1. **🥇 Enrollment Entity** - Start here! Gets course grades in one API call
2. **Course Entity** - Basic course display and identification
3. **Assignment Entity** - Enable "Due Soon" functionality  
4. **Submission Entity** - Enable recent grades and trend analysis
5. **AssignmentGroup Entity** - Enable detailed category breakdowns

Each entity should have its own repository following the `CanvasCoursesRepository` pattern.

---

## 📋 Data Sources Summary

This section documents what data we can get directly from Canvas, what we must calculate, and what limitations exist.

### ✅ Direct from Canvas APIs (No Calculation Needed)

| Dashboard Feature | Canvas API Source | Field |
|------------------|-------------------|-------|
| **Course Percentage** | `/api/v1/users/self/enrollments` | `current_score` |
| **Letter Grade** | `/api/v1/users/self/enrollments` | `current_grade` |
| **Semester vs Graded Distinction** | `/api/v1/users/self/enrollments` | `final_score` vs `current_score` |
| **Course Names** | `/api/v1/courses` | `name`, `course_code` |
| **Assignment Details** | `/api/v1/courses/{id}/assignments` | `name`, `due_at`, `points_possible` |
| **Student Submissions** | `/api/v1/courses/{id}/students/submissions` | `score`, `graded_at`, `workflow_state` |
| **Category Names & Weights** | `/api/v1/courses/{id}/assignment_groups` | `name`, `group_weight` |

### 🧮 Must Calculate (Canvas Provides Raw Data)

| Dashboard Feature | Calculation Method | Required APIs |
|------------------|-------------------|---------------|
| **Category Breakdown Scores** | Group submissions by `assignment_group_id`, calculate average | Submissions + Assignment Groups |
| **Raw Points Totals** | Sum `assignments.pointsPossible` and `submissions.score` | Assignments + Submissions |
| **Assignment Counts** | Count submissions by `workflowState` ("graded", "submitted", etc.) | Submissions |
| **Recent Grades (5)** | Sort submissions by `graded_at DESC`, take first 5 | Submissions |
| **Due Soon Count** | Filter assignments where `due_at` is within next 7 days | Assignments |
| **Grade Trends** | Compare recent submission percentages over time | Submissions (historical) |
| **Semester Points Summary** | Sum all assignment points vs all submission scores | Assignments + Submissions |

### ❌ Cannot Obtain (API Limitations)

| Dashboard Feature | Limitation | Recommended Solution |
|------------------|------------|---------------------|
| **Class Averages** | Admin-only endpoints, not accessible to students | Remove from dashboard OR use mock data (82.3%) |
| **Historical Course Data** | Enrollments API only returns active enrollments | Current semester data only (acceptable) |
| **Real-time Notifications** | No webhook/push notification APIs for students | Polling on dashboard refresh only |

### 🎯 Implementation Strategy

**Phase 1 - Core Features (Canvas Direct):**
- Course percentages and grades ✅
- Basic course information ✅ 
- Assignment due dates ✅

**Phase 2 - Calculated Features:**
- Category breakdown scores 🧮
- Points totals and summaries 🧮
- Assignment counts and recent grades 🧮

**Phase 3 - Enhanced Features:**
- Grade trend analysis 🧮
- Smart status indicators 🧮

**Not Implemented:**
- Class average comparisons ❌
- Cross-semester historical data ❌

This approach ensures we build what's possible and set proper expectations for features that require institutional API access.