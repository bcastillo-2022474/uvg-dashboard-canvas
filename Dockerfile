# Simple single-stage Dockerfile for faster builds
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy project files
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/canvas-dashboard-0.0.1-SNAPSHOT.jar"]