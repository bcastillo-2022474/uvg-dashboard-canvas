# Canvas Dashboard

A modern, high-performance dashboard for Canvas LMS that provides grade analytics, trend analysis, and predictive insights for students.

## What It Does

Canvas Dashboard fetches your course data from Canvas LMS and presents it in an intuitive, single-page dashboard with:

- **Real-time Grade Overview** - Current grades across all courses with visual indicators
- **Category Breakdown** - Performance by assignment type (exams, labs, projects) with weighted percentages
- **Trend Analysis** - Identifies if your grades are improving, declining, or stable based on recent submissions
- **Predictive Analytics** - Uses linear regression on your grade history to predict final course grades
- **Upcoming Assignments** - Next 7 days of due dates across all courses
- **Grade Progression Chart** - Visual representation of cumulative grade changes over time

### How It Calculates

1. **Grade Aggregation**: Fetches enrollment data with Canvas-computed grades in parallel (one API call per course)
2. **Category Analysis**: Groups assignments by type, calculates earned/possible points, and computes weighted percentages
3. **Trend Detection**: Compares first half vs second half of recent grades to detect direction (up/down/stable with 5% threshold)
4. **Predictive Scoring**:
   - Performs linear regression on graded assignments (score vs time)
   - Projects predicted scores for ungraded assignments based on due dates
   - Calculates final predicted percentage: `(earned + predicted) / total × 100`
   - Converts to letter grade using standard scale (93+ = A, 90-92 = A-, etc.)

### Why It's Useful

Canvas's native interface requires clicking through multiple pages to understand your academic standing. This dashboard:
- **Saves Time**: All critical information in one view (5+ Canvas pages condensed to 1)
- **Provides Insight**: Trend analysis and predictions help you identify courses needing attention
- **Enables Planning**: See remaining points and upcoming deadlines to strategize your efforts
- **Reduces Stress**: Clear visual feedback on performance across all courses at a glance

## Quick Start

### Prerequisites
- **Java 21** - [Download Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21)
- **Canvas API Token** - Generate from Canvas: Account → Settings → New Access Token

### Configuration

1. Create `src/main/resources/application.properties`:
```properties
# Canvas Configuration
canvas.instance.url=https://canvas.instructure.com
canvas.api.token=YOUR_CANVAS_API_TOKEN_HERE

# Server Configuration
server.port=8080
```

2. Run the application:
```bash
mvn spring-boot:run
```

3. Access at `http://localhost:8080` and login with your Canvas API token

### Using Maven Wrapper (No Maven Installation)

**Windows:**
```bash
mvnw.cmd spring-boot:run
```

**macOS/Linux:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

## Technical Architecture

### Port and Adapter Pattern (Hexagonal Architecture)

The application follows hexagonal architecture to isolate business logic from external dependencies:

```
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN CORE                            │
│  ┌───────────────────────────────────────────────────┐     │
│  │ Services                                          │     │
│  │  - DashboardService (orchestrates data fetching) │     │
│  │  - PredictionService (grade predictions)         │     │
│  └─────────────┬──────────────────────────────────┬─┘     │
│                │                                  │        │
│  ┌─────────────▼────────────┐    ┌───────────────▼─────┐  │
│  │ Ports (Interfaces)       │    │ DTOs                │  │
│  │  - CourseRepository      │    │  - DashboardData    │  │
│  │  - EnrollmentRepository  │    │  - PredictionData   │  │
│  │  - AssignmentRepository  │    │  - CourseCardData   │  │
│  │  - SubmissionRepository  │    └─────────────────────┘  │
│  │  - AssignmentGroupRepo   │                             │
│  └──────────────────────────┘                             │
└──────────────────┬──────────────────────┬──────────────────┘
                   │                      │
       ┌───────────▼─────────┐  ┌─────────▼────────────┐
       │ ADAPTERS (Canvas)   │  │ ADAPTERS (Mock)      │
       │  - CanvasCourseRepo │  │  - MockCourseRepo    │
       │  - CanvasEnrollRepo │  │  - MockEnrollRepo    │
       │  - CanvasAssignRepo │  │  - MockAssignRepo    │
       │  - CanvasSubmitRepo │  │  - MockSubmitRepo    │
       │  - CanvasGroupRepo  │  │  - MockGroupRepo     │
       └─────────────────────┘  └──────────────────────┘
```

**Key Benefits:**
- **Testability**: Services depend on interfaces (ports), not implementations. Tests mock repositories easily.
- **Parallel Development**: Different adapters (Canvas vs Mock) can be developed/tested independently

**How It Works:**
1. **Ports** (in `repositories/`): Define contracts (interfaces) for data access
2. **Adapters** (in `adapters/`): Implement ports for specific data sources (Canvas API, mock data)
3. **Services** (in `services/`): Contain business logic, depend only on ports
4. **Spring IoT**: Injects appropriate adapter implementation at runtime

### MVC Pattern

The application uses Spring MVC for the web layer:

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│   Browser    │◄──────┤    View      │◄──────┤  Controller  │
│   (Client)   │       │  (Thymeleaf) │       │              │
└──────────────┘       └──────────────┘       └───────┬──────┘
                                                       │
                                               ┌───────▼──────┐
                                               │   Services   │
                                               │    (Model)   │
                                               └──────────────┘
```

**Flow:**
1. **Controller** (`DashboardController.java`):
   - Handles HTTP requests (`GET /dashboard`)
   - Extracts authentication details from Spring Security context
   - Calls service layer to fetch data
   - Populates model and returns view name

2. **Model** (DTOs in `model/dto/`):
   - `DashboardData` - Complete dashboard state
   - `CourseCardData` - Per-course information
   - `PredictionData` - Grade predictions
   - Public fields for Thymeleaf access (no boilerplate getters)

3. **View** (`templates/dashboard.html`):
   - Thymeleaf template with `${...}` expressions
   - Tailwind CSS for styling
   - Lucide icons for visual elements

**Security:**
- Custom `CanvasAuthenticationProvider` validates API tokens against Canvas
- User details stored in Spring Security context
- All routes except `/login` require authentication

## Project Structure

```
src/main/java/com/bestprograteam/canvas_dashboard/
├── config/                          # Spring Security configuration
│   └── CanvasAuthenticationProvider.java
├── controller/                      # MVC Controllers
│   ├── DashboardController.java     # Main dashboard endpoint
│   └── ErrorController.java         # Error handling
├── model/
│   ├── dto/                         # Data Transfer Objects
│   │   ├── DashboardData.java       # Top-level dashboard data
│   │   ├── CourseCardData.java      # Per-course card data
│   │   ├── PredictionData.java      # Grade predictions
│   │   ├── RecentGrade.java         # Submission + Assignment pair
│   │   ├── CategoryBreakdown.java   # Assignment group stats
│   │   └── ...
│   ├── entities/                    # Domain entities
│   │   ├── Course.java
│   │   ├── Enrollment.java
│   │   ├── Assignment.java
│   │   ├── Submission.java
│   │   └── AssignmentGroup.java
│   ├── repositories/                # Ports (interfaces)
│   │   ├── CourseRepository.java
│   │   ├── EnrollmentRepository.java
│   │   └── ...
│   ├── adapters/                    # Adapter implementations
│   │   ├── CanvasCourseRepository.java
│   │   ├── MockCourseRepository.java
│   │   └── ...
│   └── services/                    # Business logic
│       ├── DashboardService.java    # Data aggregation
│       └── PredictionService.java   # Grade predictions
└── ...

src/main/resources/
├── templates/                       # Thymeleaf views
│   ├── dashboard.html               # Main dashboard
│   ├── login.html                   # Login page
│   └── error/                       # Error pages
└── application.properties           # Configuration

src/test/java/                       # Tests
└── model/services/
    ├── DashboardServiceTest.java    # 11 test cases with mocked repos
    └── PredictionServiceTest.java   # 14 test cases for predictions
```

## Development

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=DashboardServiceTest

# Run with coverage
mvn clean test jacoco:report
```

### Technology Stack

- **Backend**: Spring Boot 3.5.5, Spring Security, Spring MVC
- **Frontend**: Thymeleaf, HTMX, Tailwind CSS, Lucide Icons
- **Testing**: JUnit 5, Mockito, AssertJ
- **Build**: Maven
- **Architecture**: Hexagonal (Ports & Adapters) + MVC

### Key Design Decisions

1. **Public Fields Over Getters/Setters**: DTOs and entities use public fields to reduce boilerplate (400+ lines removed)
2. **Parallel Data Fetching**: `CompletableFuture` with 20-thread pool for fast Canvas API calls
3. **Security Context Propagation**: `DelegatingSecurityContextExecutorService` ensures authentication in async threads
4. **Direct Field Access**: Services access entity fields directly (e.g., `course.name` not `course.getName()`)
5. **No Persistence Layer**: Read-only application fetches fresh data from Canvas on each request

## Troubleshooting

**Port 8080 already in use:**
```bash
# Change port in application.properties
server.port=8081
```

**Canvas API Authentication Failed:**
- Verify your token is valid in Canvas
- Check `canvas.instance.url` matches your Canvas domain
- Ensure token has not expired

**Tests Failing:**
```bash
# Clean and rebuild
mvn clean install
```

**"mvn: command not found":**
Use Maven wrapper: `./mvnw` (Unix) or `mvnw.cmd` (Windows)

## License

Educational project for Canvas LMS integration demonstration.
