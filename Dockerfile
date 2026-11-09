# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml .
COPY .mvn .mvn

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline

COPY config config
COPY src src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 3050

ENTRYPOINT ["java", "-jar", "app.jar"]
