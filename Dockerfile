
FROM gradle:8.4.0-jdk17 AS builder
WORKDIR /app

COPY . .


RUN gradle build -x test

FROM eclipse-temurin:23-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
