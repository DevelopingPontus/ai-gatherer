# Stage 1: Build
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY entrypoint.sh .

RUN chmod +x entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["./entrypoint.sh"]
