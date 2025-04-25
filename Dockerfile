
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test --stacktrace --info


RUN echo ">>> BUILDER /app:" && ls -R /app

FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
