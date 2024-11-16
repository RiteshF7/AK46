FROM openjdk:11-jre-slim as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
EXPOSE 8080:8080
RUN gradle buildFatJar --no-daemon