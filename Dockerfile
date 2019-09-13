FROM openjdk:8-jre-alpine
MAINTAINER devcsrj

RUN apk add --no-cache tzdata

ARG JAR=build/libs/ispmon-MISSING.jar
COPY $JAR /opt/ispmon.jar

EXPOSE 5000
VOLUME /opt/results

WORKDIR /opt
ENTRYPOINT ["java", \
"-server", \
"-XX:+UnlockExperimentalVMOptions", \
"-XX:InitialRAMFraction=2", \
"-XX:MinRAMFraction=2", \
"-XX:MaxRAMFraction=2", \
"-XX:MaxGCPauseMillis=100", \
"-XX:+UseStringDeduplication", \
"-jar", \
"ispmon.jar"]
