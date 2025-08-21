# Use a lightweight OpenJDK image
FROM openjdk:21-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy Maven build output (the jar) into container
COPY target/inventoryservice-0.0.1-SNAPSHOT.jar app.jar

# Expose application port (Spring Boot default: 8080)
EXPOSE 9093

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
