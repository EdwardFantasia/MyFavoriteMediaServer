# Stage 1: Build the Application
FROM gradle:jdk25 AS build
WORKDIR /app

# Copy the Gradle wrapper files AND build config FIRST
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# Resolve and cache dependencies + Gradle distribution
RUN ./gradlew dependencies --no-daemon

# Copy source code ONLY AFTER caching dependencies
COPY src/ src/

# Build the jar using cached layers
RUN ./gradlew bootJar --no-daemon

# Stage 2: Create the Lightweight Runtime Image
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]