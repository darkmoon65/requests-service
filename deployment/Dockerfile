FROM gradle:8.9-jdk21 AS build

WORKDIR /app
COPY . .

RUN ./gradlew :app-service:bootJar -x validateStructure --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /app/applications/app-service/build/libs/*.jar app.jar

USER 1001

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]