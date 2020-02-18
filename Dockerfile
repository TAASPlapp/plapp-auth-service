FROM openjdk:8-jdk-alpine
COPY . /auth-service
WORKDIR /auth-service
CMD ./mvnw package -DskipTests
ENTRYPOINT ["java", "-jar", "target/auth-service-0.0.1-SNAPSHOT.jar"]