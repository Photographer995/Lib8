
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app


COPY . .
RUN chmod +x gradlew


RUN ./gradlew build -x test --stacktrace --info


FROM eclipse-temurin:23-jre
WORKDIR /app

COPY --from=builder /app/build/libs/bsuir2-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
