FROM openjdk:8
MAINTAINER Caspar
LABEL app="gateway-service" version="0.0.1" by="caspar"
COPY ./build/libs/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar
CMD java -jar gateway-service.jar --spring.profiles.active=${env}
