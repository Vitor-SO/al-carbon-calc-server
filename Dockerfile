
FROM gradle:8.7-jdk17 AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:17-jre-focal

WORKDIR /app

COPY . /app

EXPOSE 8085

COPY --from=build /app/build/libs/*.jar ./carboncalc.jar

ENTRYPOINT ["java", "-jar", "carboncalc.jar"]
