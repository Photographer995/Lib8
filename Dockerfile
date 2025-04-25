
FROM openjdk:23-jdk-slim AS builder
WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew build -x test --stacktrace --info

FROM openjdk:23-jre-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
