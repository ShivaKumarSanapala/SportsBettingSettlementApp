# Multi-stage Docker build for Spring Boot application
# Support multiple platforms: linux/amd64, linux/arm64
FROM --platform=$BUILDPLATFORM maven:3.9.4-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM --platform=$TARGETPLATFORM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN useradd -r -u 1001 springboot

# Copy the built jar from build stage
COPY --from=build /app/target/sports-betting-settlement-1.0.0.jar app.jar

# Change ownership to non-root user
RUN chown springboot:springboot app.jar

# Switch to non-root user
USER springboot

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
