# Use Java 24 base image
FROM eclipse-temurin:24-jdk

# Set working directory
WORKDIR /app

# Copy project source files
COPY . .

# Build the JAR file inside the Docker container
RUN ./mvnw clean package -DskipTests

# Run the app
ENTRYPOINT ["java", "-jar", "target/blockvote-0.0.1-SNAPSHOT.jar"]
