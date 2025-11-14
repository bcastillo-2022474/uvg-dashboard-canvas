# Prediction Service Implementation

## 1. Overview

The `PredictionService` is responsible for analyzing a student's historical grade data to forecast their final grade in a course. It uses a linear regression model to identify performance trends and project scores for future assignments. This document outlines the technical implementation of this service.

## 2. Data Structures

The service will expose its results through the following Data Transfer Objects (DTOs).

### `PredictionData.java`

This is the primary DTO returned by the service.

```java
public class PredictionData {
    // The final predicted percentage for the semester (e.g., 88.5)
    private double predictedScore;
    // The corresponding letter grade (e.g., "B+")
    private String predictedLetterGrade;
    // A list of data points for the historical grade chart
    private List<ChartDataPoint> gradeProgression;
    // Indicates if a prediction was successfully generated
    private boolean isPredictionAvailable;
}
```

### `ChartDataPoint.java`

Represents a single point in the historical grade progression chart.

```java
public class ChartDataPoint {
    private String label; // e.g., "Week 5"
    private double score; // The student's cumulative score at that point in time
}
```

## 3. Prediction Algorithm: Linear Regression

The core of the service is a linear regression model (`y = mx + c`) that is calculated from scratch.

### Step 1: Data Preparation

1.  **Aggregate Data:** All assignments from all courses in the `DashboardData` object are collected.
2.  **Filter for Graded Assignments:** A list is created containing only assignments that have a submission with a valid score and have `pointsPossible > 0`.
3.  **Sort Chronologically:** This list is sorted by the submission date (`gradedAt`) to create a time-series.
4.  **Define Axes:**
    *   **Time (X-axis):** Represented as the number of days since the first graded assignment in the series. The first assignment is at `x=0`.
    *   **Score (Y-axis):** Represented as the percentage score for each assignment: `(submission.score / assignment.pointsPossible) * 100`.

### Step 2: Linear Regression Calculation

The slope (`m`) and y-intercept (`c`) of the trend line are calculated using the following formulas:

*   **Slope `m`:** `(n * Σ(xy) - Σx * Σy) / (n * Σ(x²) - (Σx)²) `
*   **Y-intercept `c`:** `(Σy - m * Σx) / n`

Where:
*   `n` = number of data points
*   `Σx` = sum of all x-values
*   `Σy` = sum of all y-values
*   `Σxy` = sum of the products of each `x*y` pair
*   `Σ(x²)` = sum of the squares of all x-values

### Step 3: Predict Future Scores

1.  **Identify Ungraded Assignments:** A list is created of all assignments that have not yet been graded.
2.  **Project Scores:** For each ungraded assignment, its time value (`x`) is determined from its `dueAt` date. This `x` is used in the regression formula `y = mx + c` to predict the score.
3.  **Clamp Results:** Predicted scores are clamped to ensure they fall within a realistic `0-100` range.

### Step 4: Calculate Final Predicted Grade

The final grade is calculated by combining actual and predicted scores:

`Final Grade = (Sum of Actual Scores + Sum of Predicted Scores) / Total Possible Points in Semester`

## 4. Grade Progression Chart

The historical grade progression chart is generated separately:
1.  The chronologically sorted list of graded assignments is iterated through.
2.  After each assignment, the student's cumulative grade *at that point in time* is calculated.
3.  A `ChartDataPoint` is created for that point in time and added to the `gradeProgression` list.

## 5. Business Rules and Edge Cases

*   **Minimum Data Points:** A prediction is only calculated if there are **5 or more graded assignments**. If this condition is not met, the `isPredictionAvailable` flag in the `PredictionData` DTO will be set to `false`, and the UI should display a message like "Not enough data for a prediction."
*   **No Fallbacks:** The service will not fall back to simpler prediction models. It will only provide a prediction if the linear regression model can be successfully applied.
*   **Consistency:** The method of calculation is applied consistently. There are no alternative paths that would produce a prediction using a different technique.
