# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine
# Set the working directory inside the container
WORKDIR /app

# Copy built jar (use correct jar name after build)
COPY target/api-gateway*.jar app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]