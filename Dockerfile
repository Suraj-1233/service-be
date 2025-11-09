# ============================
# 1️⃣ Build Stage
# ============================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and preload dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the app (skip tests for speed)
RUN mvn clean package -DskipTests

# ============================
# 2️⃣ Runtime Stage
# ============================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy only the built jar (no source code)
COPY --from=build /app/target/*.jar app.jar

# Optional: accept a Firebase key via build arg (for CI/CD)
ARG FIREBASE_KEY_BASE64
RUN if [ -n "$FIREBASE_KEY_BASE64" ]; then \
      echo "$FIREBASE_KEY_BASE64" | base64 -d > /app/serviceAccountKey.json; \
    fi

# Expose port
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
