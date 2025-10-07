package com.bestprograteam.canvas_dashboard.services;

import com.bestprograteam.canvas_dashboard.model.entities.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para interactuar con Canvas API.
 */
public class CanvasApiService {

    private String baseUrl;
    private String apiVersion;
    private float timeout;
    private int retryAttempts;
    private String currentToken;

    public CanvasApiService(String baseUrl, String apiVersion, float timeout, int retryAttempts) {
        this.baseUrl = baseUrl;
        this.apiVersion = apiVersion;
        this.timeout = timeout;
        this.retryAttempts = retryAttempts;
    }

    // --- Métodos de autenticación y fetch ---
    public CompletableFuture<User> authenticateUser(String username, String password) {
        // Aquí se implementaría la llamada HTTP a Canvas
        return CompletableFuture.completedFuture(
                new User("1", username, username + "@canvas.com", "", "token", null, true)
        );
    }

    public CompletableFuture<List<Course>> getCourses(String userId) {
        return CompletableFuture.completedFuture(List.of());
    }

    public CompletableFuture<List<Assignment>> getAssignments(String courseId) {
        return CompletableFuture.completedFuture(List.of());
    }

    public CompletableFuture<List<Submission>> getSubmissions(String assignmentId, String userId) {
        return CompletableFuture.completedFuture(List.of());
    }

    public CompletableFuture<List<GradingCategory>> getGradingCategories(String courseId) {
        return CompletableFuture.completedFuture(List.of());
    }

    public CompletableFuture<Feedback> getFeedback(String submissionId) {
        return CompletableFuture.completedFuture(new Feedback());
    }

    // --- Getters y Setters ---
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }

    public float getTimeout() { return timeout; }
    public void setTimeout(float timeout) { this.timeout = timeout; }

    public int getRetryAttempts() { return retryAttempts; }
    public void setRetryAttempts(int retryAttempts) { this.retryAttempts = retryAttempts; }

    public String getCurrentToken() { return currentToken; }
    public void setCurrentToken(String currentToken) { this.currentToken = currentToken; }
}
