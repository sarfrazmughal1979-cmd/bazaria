# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY shared-core shared-core/
COPY iam-module iam-module/
COPY catalog-module catalog-module/
COPY inventory-module inventory-module/
COPY cart-module cart-module/
COPY order-module order-module/
COPY payment-module payment-module/
COPY shipping-module shipping-module/
COPY promotion-module promotion-module/
COPY notification-module notification-module/
COPY settlement-module settlement-module/
COPY analytics-module analytics-module/
COPY cms-module cms-module/
COPY support-module support-module/
COPY platform-app platform-app/
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/platform-app/target/platform-app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
