FROM maven:3.9-eclipse-temurin-21-alpine

WORKDIR /app

COPY target/RestaurantManagement-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]