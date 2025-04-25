
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app


COPY . .
RUN chmod +x gradlew


RUN ./gradlew build -x test --stacktrace --info


FROM eclipse-temurin:23-jre
WORKDIR /app

RUN echo "=== /app contains ===" && ls -al /app && echo "=== /app/build/libs contains ===" && ls -al /app/build/libs


ENTRYPOINT ["java", "-jar", "app.jar"]
