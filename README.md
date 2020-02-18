# plapp-auth-service

Authentication microservice

## Building and Running

    ./mvnw package -DskipTests
    docker-compose up
    
Recompiling plapp-auth-service

    ./mvnw package -DskipTests && docker-compose up --build plapp-auth-service

