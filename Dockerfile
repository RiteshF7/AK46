# Build stage
FROM gradle:7.4.2-jdk11 AS build

# Copy the project files into the container
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Build the application with Gradle
RUN gradle buildFatJar --no-daemon

# Runtime stage
FROM openjdk:11-jre-slim

# Set environment variable for the port
ENV PORT=8080
EXPOSE $PORT

# Copy the built JAR file from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
WORKDIR /app

# Command to run the application
CMD java -jar app.jar