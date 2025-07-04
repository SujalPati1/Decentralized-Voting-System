# Use Java 24 base image
FROM eclipse-temurin:24-jdk

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

#Grant execute permission to mvnw script
RUN chmod +x mvnw

# Build the JAR file
RUN ./mvnw clean package -DskipTests

# Run the app
ENTRYPOINT ["java", "-jar", "target/blockvote-0.0.1-SNAPSHOT.jar"]
