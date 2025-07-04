FROM eclipse-temurin:24-jdk

LABEL maintainer="Sujal Patil"

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]