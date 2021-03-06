# build stage build the jar with all our resources
FROM maven:slim as build

RUN apt-get update; apt-get -y install git;

WORKDIR /workspace
RUN git clone https://github.com/victorscavalcante14/POCCamelSpringBoot.git
WORKDIR /workspace/POCCamelSpringBoot
RUN mvn install

# package stage
FROM openjdk:8-jdk-alpine as package
WORKDIR /# copy only the built jar and nothing else
COPY --from=build /workspace/POCCamelSpringBoot/target/POCCamelSpringBoot-0.1.jar /app.jar

EXPOSE 8080

# production stage
FROM package as production

ENTRYPOINT ["sh","-c","java -jar -Dspring.profiles.active=production /app.jar"]

# remote debug stage
FROM package as debug

EXPOSE 8888

ENTRYPOINT ["sh","-c","java -Xdebug -Xrunjdwp:transport=dt_socket,address=8888,server=y,suspend=n -jar /app.jar"]