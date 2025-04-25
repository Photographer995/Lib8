
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app


COPY . .


RUN echo ">>> Список файлов после COPY:" && ls -l

RUN chmod +x gradlew

RUN ./gradlew bootJar -x test --stacktrace --info


RUN echo ">>> Содержимое build/libs:" && ls -l /app/build/libs

FROM eclipse-temurin:23-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
