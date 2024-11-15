FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
EXPOSE 8080:8080
RUN gradle buildFatJar --no-daemon