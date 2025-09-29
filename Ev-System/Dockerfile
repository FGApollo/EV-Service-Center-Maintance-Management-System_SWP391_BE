# ==========================================
# Stage 1: Build
# ==========================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Fix quyền cho mvnw
RUN chmod +x mvnw

# Build ứng dụng bằng Maven Wrapper
RUN ./mvnw clean package -DskipTests

# ==========================================
# Stage 2: Run
# ==========================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy file jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Expose cổng (mặc định Spring Boot chạy 8080)
EXPOSE 8080

# Lệnh chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
