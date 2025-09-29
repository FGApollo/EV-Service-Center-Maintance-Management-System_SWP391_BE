# Sử dụng JDK 17
FROM eclipse-temurin:17-jdk-alpine as builder

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy toàn bộ source code
COPY . .

# Build ứng dụng bằng Maven Wrapper
RUN ./mvnw clean package -DskipTests

# ==========================================
# Giai đoạn chạy app
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy file jar từ giai đoạn build
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Lệnh chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
