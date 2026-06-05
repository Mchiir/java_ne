# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

# Copy POM first so dependency layer is cached separately from source code
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build the fat JAR (skip tests — tests run in CI)
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S ubms && adduser -S ubms -G ubms

# Copy the executable JAR from the build stage
COPY --from=build /workspace/target/utility-billing-system-*.jar app.jar

# Switch to non-root user
USER ubms

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
