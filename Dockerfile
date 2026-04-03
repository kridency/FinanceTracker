FROM eclipse-temurin:17-jdk

WORKDIR /app

ENV POSTGRES_DATASOURCE_URL='jdbc:postgresql://postgres-container.docker_default:5432/tracker_db?currentSchema=custom&createDatabaseIfNotExist=true'

COPY target/FinanceTracker-1.0.0-SNAPSHOT.jar app.jar

ENV CREATE_ON_STARTUP=false

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
