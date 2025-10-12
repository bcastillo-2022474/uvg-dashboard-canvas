# Dashboard View Integration Plan

## 📊 View Data Requirements Analysis

### **Top Summary Widgets** (4 widgets across top)
| Widget | Data Needed | Source | Status |
|--------|-------------|--------|--------|
| **Overall Semester** | Average percentage across all courses | `SemesterSummary.overallPercentage` | ✅ Available |
| **Graded Points** | Total graded points earned/possible | Calculate from all submissions | ⚠️ Needs calculation |
| **Completed Assignments** | Count of graded submissions | Calculate from all submissions | ⚠️ Needs calculation |
| **Due Soon** | Count of assignments due in 7 days | `SemesterSummary.upcomingAssignments` | ✅ Available |

### **Additional Widgets Row** (3 widgets)
| Widget | Data Needed | Source | Status |
|--------|-------------|--------|--------|
| **This Week** | Tasks completed this week, points earned | Custom calculation | ❌ Not implemented |
| **Predicted Grade** | Projected final percentage | Use `enrollment.finalScore` | ⚠️ Partial |
| **Points Summary** | Semester total, graded total, remaining | Calculate across all courses | ⚠️ Needs calculation |

### **Course Cards** (Repeated for each course)
| Field | Data Needed | Source | Status |
|-------|-------------|--------|--------|
| **Course Name** | Full name | `Course.name` | ✅ Available |
| **Course Code** | Short code (e.g., "CS2021") | `Course.code` | ✅ Available |
| **Color Indicator** | Random/assigned color | Mock/generate | ❌ Not implemented |
| **Letter Grade** | Letter grade | `Enrollment.currentGrade` | ✅ Available |
| **Current Percentage** | Percentage | `Enrollment.currentScore` | ✅ Available |
| **Trend Icon** | up/down/stable | `GradeService.calculateGradeTrend()` | ✅ Available |
| **Semester Points** | Points earned/total | `GradeService.calculatePointsSummary()` | ✅ Available |
| **Graded Points** | Graded points earned/total | From submissions | ⚠️ Needs calculation |
| **Class Average** | Class average % | ❌ Canvas API limitation | ❌ Use mock/hide |
| **Assignment Count** | Completed/total | `AssignmentService.getAssignmentCountsByStatus()` | ✅ Available |
| **Recent Grades** | 5 recent graded items | `GradeService.getRecentGrades()` | ✅ Available |
| **Category Breakdown** | Categories with scores | `CategoryService.getCategoryBreakdown()` | ✅ Available |
| **Upcoming Assignments** | Due soon for this course | `AssignmentService.getUpcomingAssignmentsByCourse()` | ✅ Available |

### **Grade Progression Chart**
| Data | Source | Status |
|------|--------|--------|
| Weekly grade percentages | Historical submissions | ❌ Not implemented |

---

## 🎯 Data Availability Assessment

### ✅ **Already Available from Services:**
- Course basic info (name, code)
- Enrollment grades (currentScore, currentGrade)
- Trend calculation (up/down/stable)
- Recent grades (top 5)
- Category breakdown with scores
- Upcoming assignments per course
- Overall semester percentage

### ⚠️ **Needs Additional Calculation:**
- **Total graded points** across all courses
- **Total completed assignments** across all courses
- **Graded vs Semester points** distinction per course
- **Assignment status counts** (graded/unsubmitted/total)
- **Remaining points** calculation

### ❌ **Cannot Implement (API Limitations):**
- **Class averages** - Admin-only Canvas API
- **Weekly progression chart** - Need historical tracking (not in current scope)
- **This week's tasks** - Need date filtering (complex)

---

## 🏗️ Architecture Decision: Monolithic vs Component-Based

### **Option 1: Single Controller Load (Current Approach)**
**Pros:**
- ✅ Simple - one endpoint loads everything
- ✅ Consistent data snapshot (no race conditions)
- ✅ Standard Spring MVC pattern
- ✅ Works perfectly for read-only dashboard
- ✅ Matches current Thymeleaf server-side rendering

**Cons:**
- ❌ Slower initial page load (all data fetched upfront)
- ❌ Can't lazy-load course cards
- ❌ Full page reload on refresh

### **Option 2: Component-Based with HTMX**
**Pros:**
- ✅ Faster initial load (summary first, cards lazy-load)
- ✅ Can refresh individual course cards
- ✅ Better perceived performance

**Cons:**
- ❌ More complex (multiple endpoints per course)
- ❌ Inconsistent data snapshots
- ❌ More API calls to Canvas
- ❌ Overkill for 4-5 courses

### **🎯 RECOMMENDATION: Option 1 - Single Controller Load**

**Rationale:**
1. Student has only 4-5 active courses (not 50)
2. Read-only dashboard doesn't need real-time updates
3. Full data snapshot ensures consistency
4. Simpler to implement and maintain
5. Standard Spring MVC + Thymeleaf pattern

**Future Enhancement:** If needed, add HTMX for:
- Collapsible sections (recent grades)
- "Refresh" button per course card
- But keep initial load monolithic

---

## 📐 Data Flow Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    DashboardController                       │
│                    GET /dashboard                            │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                   DashboardService                           │
│             getDashboardData()                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Orchestrates all service calls                       │   │
│  └──────────────────────────────────────────────────────┘   │
└───┬────────────┬────────────┬────────────┬─────────────────┘
    │            │            │            │
    ▼            ▼            ▼            ▼
┌────────┐  ┌──────────┐ ┌─────────┐ ┌──────────────┐
│Course  │  │Assignment│ │Grade    │ │Category      │
│Service │  │Service   │ │Service  │ │Service       │
└────┬───┘  └────┬─────┘ └────┬────┘ └──────┬───────┘
     │           │            │             │
     ▼           ▼            ▼             ▼
┌────────────────────────────────────────────────────┐
│              Repository Layer                       │
│  (CourseRepo, EnrollmentRepo, AssignmentRepo,     │
│   SubmissionRepo, AssignmentGroupRepo)            │
└────────────────────────────────────────────────────┘
```

---

## 🔧 Implementation Plan

### **Phase 1: Enhance DashboardService** ⚡
**Goal:** Make DashboardService return COMPLETE course card data

**Current:**
```java
DashboardData {
  courses: List<CourseWithGrades>  // Only course + enrollment
  upcomingAssignments: List<Assignment>
  summary: SemesterSummary
}
```

**Enhanced:**
```java
DashboardData {
  courseCards: List<CourseCardData>  // FULL course cards with everything
  upcomingAssignments: List<Assignment>
  summary: SemesterSummary (enhanced with totals)
}
```

**Changes needed:**
1. Update `DashboardService.getDashboardData()` to call `getCourseCardData()` for each course
2. Enhance `SemesterSummary` to include:
   - `totalCompletedAssignments` (across all courses)
   - `totalGradedPoints` and `totalGradedPointsPossible`
   - `totalSemesterPoints` and `totalSemesterPointsPossible`
3. Add helper methods for:
   - Calculating total graded points
   - Counting total completed assignments

---

### **Phase 2: Add Missing Calculations** 🧮

**Add to GradeService:**
```java
// Calculate graded points separately from semester points
public PointsSummary calculateGradedPointsSummary(Integer courseId);

// Get total assignment counts
public Map<String, Integer> getAssignmentCountsByCourse(Integer courseId);
```

**Add to DashboardService:**
```java
// Calculate semester-wide totals
private SemesterTotals calculateSemesterTotals(List<CourseCardData> courseCards);
```

---

### **Phase 3: Update Thymeleaf Template** 🎨

**Replace hardcoded sections with Thymeleaf bindings:**

#### **Top Widgets:**
```html
<!-- Overall -->
<p th:text="${dashboardData.summary.overallPercentage} + '%'">86.8%</p>

<!-- Graded -->
<p th:text="${dashboardData.summary.totalGradedPercentage} + '%'">91.8%</p>
<p th:text="${dashboardData.summary.totalGradedPoints} + '/' + ${dashboardData.summary.totalGradedPointsPossible}">1847/2007</p>

<!-- Completed -->
<p th:text="${dashboardData.summary.totalCompletedAssignments}">50</p>

<!-- Due Soon -->
<p th:text="${dashboardData.summary.upcomingAssignments}">5</p>
```

#### **Course Cards Loop:**
```html
<div th:each="courseCard : ${dashboardData.courseCards}"
     class="...course card classes...">

  <!-- Course Header -->
  <h3 th:text="${courseCard.courseWithGrades.course.name}">Course Name</h3>
  <p th:text="${courseCard.courseWithGrades.course.code}">CS2021</p>

  <!-- Grade Display -->
  <span th:text="${courseCard.courseWithGrades.enrollment.currentGrade}">B+</span>
  <span th:text="${courseCard.courseWithGrades.enrollment.currentScore} + '%'">87.7%</span>

  <!-- Trend Icon -->
  <i th:data-lucide="${courseCard.trend == 'up' ? 'trending-up' :
                      courseCard.trend == 'down' ? 'trending-down' :
                      'minus'}"></i>

  <!-- Recent Grades -->
  <div th:each="recentGrade : ${courseCard.recentGrades}">
    <span th:text="${recentGrade.assignment.name}">Assignment</span>
    <span th:text="${recentGrade.submission.score} + '/' +
                   ${recentGrade.assignment.pointsPossible}">46/50</span>
  </div>

  <!-- Category Breakdown -->
  <div th:each="category : ${courseCard.categoryBreakdown}">
    <span th:text="${category.group.name} + ' (' +
                   ${category.group.groupWeight} + '%)'">Exams (40%)</span>
    <span th:text="${category.currentScore} + '%'">84%</span>
    <span th:text="${category.pointsEarned} + '/' +
                   ${category.pointsTotal}">168/200</span>
  </div>

  <!-- Upcoming Assignments -->
  <div th:each="assignment : ${courseCard.upcomingAssignments}">
    <span th:text="${assignment.name}">Assignment Name</span>
    <span th:text="${#temporals.format(assignment.dueAt, 'MMM dd')}">Jan 22</span>
    <span th:text="${assignment.pointsPossible} + ' pts'">100 pts</span>
  </div>
</div>
```

---

### **Phase 4: Handle Missing/Mock Data** 🎭

**For features we CAN'T implement:**

1. **Class Averages** - Remove or show as "N/A"
```html
<!-- Option 1: Hide completely -->
<div th:if="false" class="bg-purple-50 p-2 rounded text-center">
  <p class="text-purple-600 font-medium">Class Avg</p>
  <p class="font-bold text-purple-800">N/A</p>
</div>

<!-- Option 2: Show placeholder -->
<div class="bg-purple-50 p-2 rounded text-center">
  <p class="text-purple-600 font-medium">Class Avg</p>
  <p class="font-bold text-purple-800">N/A</p>
</div>
```

2. **Weekly Progression Chart** - Remove entirely or keep as static mockup
```html
<!-- Remove this entire section for now -->
```

3. **This Week Widget** - Simplify to just show upcoming count
```html
<div>
  <h3>This Week</h3>
  <span th:text="${dashboardData.summary.upcomingAssignments} + ' assignments due'">
    5 assignments due
  </span>
</div>
```

---

## 📋 Step-by-Step Implementation Checklist

### **Step 1: Enhance Services** ✅
- [ ] Add `calculateGradedPointsSummary()` to GradeService
- [ ] Add semester totals calculation to DashboardService
- [ ] Enhance `SemesterSummary` DTO with new fields
- [ ] Update `DashboardService.getDashboardData()` to use `CourseCardData`

### **Step 2: Update Controller** ✅
- [ ] Verify controller passes correct data structure to view
- [ ] Add any utility methods for formatting

### **Step 3: Update View** ✅
- [ ] Replace top 4 summary widgets with Thymeleaf bindings
- [ ] Replace course cards with `th:each` loop
- [ ] Bind all course card sections to data
- [ ] Remove/hide class average sections
- [ ] Remove weekly progression chart
- [ ] Simplify "This Week" widget

### **Step 4: Test** ✅
- [ ] Run application and verify data displays
- [ ] Check for null pointer exceptions
- [ ] Verify date formatting works
- [ ] Test with mock data
- [ ] Check responsive design still works

---

## 🎨 Thymeleaf Utilities Needed

**Date Formatting:**
```java
// In controller or utility class
model.addAttribute("dateFormatter",
  DateTimeFormatter.ofPattern("MMM dd"));
```

**Percentage Formatting:**
```html
<span th:text="${#numbers.formatDecimal(value, 1, 1)} + '%'">87.7%</span>
```

**Conditional CSS Classes:**
```html
<i th:data-lucide="${trend == 'up' ? 'trending-up' : 'trending-down'}"
   th:class="${trend == 'up' ? 'text-green-500' : 'text-red-500'}"></i>
```

---

## 🚀 Final Architecture

**Single Page Load:**
1. User hits `/dashboard`
2. `DashboardController` calls `DashboardService.getDashboardData()`
3. `DashboardService` orchestrates:
   - Gets all courses from `CourseService`
   - For EACH course, calls `getCourseCardData()` to get complete data
   - Calculates semester-wide totals
4. Returns complete `DashboardData` to view
5. Thymeleaf renders everything server-side
6. Browser receives fully-rendered HTML

**Performance Notes:**
- With mock data: Instant (in-memory)
- With Canvas API: ~2-3 seconds for 4 courses (acceptable)
- Can add loading spinner during initial load

---

## ✅ Recommended Approach

**Implementation Order:**
1. ✅ Keep single controller approach (no HTMX initially)
2. ✅ Enhance `DashboardService` to return complete `CourseCardData`
3. ✅ Add missing calculation methods to services
4. ✅ Update `SemesterSummary` DTO
5. ✅ Replace hardcoded HTML with Thymeleaf loops
6. ✅ Remove/hide unavailable features (class avg, weekly chart)
7. ✅ Test with mock data
8. 🔜 Later: Implement Canvas API adapters

**This approach:**
- ✅ Uses existing service architecture
- ✅ Requires minimal changes
- ✅ Follows Spring MVC best practices
- ✅ Easy to maintain and understand
- ✅ Works great for 4-5 courses
