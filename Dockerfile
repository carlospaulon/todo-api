# build
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# pom for cache
COPY pom.xml .
RUN mvn dependency:go-offline - B

# src and build
COPY src ./src
RUN mvn clean package -DskipTests

# run
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# jar from build
COPY --from=build /app/target/*.jar app.jar

# port
EXPOSE 8080

# run app
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
