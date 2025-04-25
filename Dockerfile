# === 1. Сборка jar-файла внутри контейнера ===
FROM gradle:8.4.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# === 2. Минимальный образ только с JRE для запуска ===
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
