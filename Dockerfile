# Step 1: Use Maven to build the JAR
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY main/java/com/example/service_marketplace .
RUN mvn clean package -DskipTests

# Step 2: Run the Spring Boot JAR
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
