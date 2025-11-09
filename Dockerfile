# ============================
# 1. Build stage
# ============================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src
COPY src/main/resources/serviceAccountKey.json src/main/resources/serviceAccountKey.json

# Build the application (skip tests to save time)
RUN mvn clean package -DskipTests

# ============================
# ============================
# 2. Runtime stage
# ============================
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar
COPY src/main/resources/serviceAccountKey.json src/main/resources/serviceAccountKey.json

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
