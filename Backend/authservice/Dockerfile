# Multi-stage build for Auth Service
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /workspace

# Copy Maven files for dependency caching
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:21-jre

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r authuser && useradd -r -g authuser authuser

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /workspace/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R authuser:authuser /app

# Switch to non-root user
USER authuser

# Expose application port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Set JVM options for better performance
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
