# Spring Boot Dockerfile
FROM eclipse-temurin:21-jre-alpine

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy JAR from Maven build (CI/CD builds before Docker)
COPY --chown=spring:spring target/finpath.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 9080

# Health check using Actuator
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
