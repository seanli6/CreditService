# Maven build
FROM maven:3.6.3-jdk-11-slim AS MAVEN_BUILD
MAINTAINER Sean Li
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package

# Build image
FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/*.jar  app.jar
ENTRYPOINT ["java","-jar","app.jar"]