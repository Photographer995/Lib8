# === 1. Сборка внутри контейнера ===
FROM gradle:8.5-jdk23 AS builder
WORKDIR /app

COPY . .
RUN gradle build --no-daemon -x test

# === 2. Мини-образ для запуска ===
FROM openjdk:23-slim
WORKDIR /app

COPY --from=builder /app/build/libs/bsuir2-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
