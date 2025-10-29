# Use Java 17 base image
FROM openjdk:17-jdk-slim

# Copy jar file
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "/app.jar"]
