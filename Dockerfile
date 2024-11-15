FROM gradle:6.9-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:8-jdk-alpine
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/secure-shield_server.jar
ENTRYPOINT ["java", "-jar", "/app/secure-shield_server.jar"]