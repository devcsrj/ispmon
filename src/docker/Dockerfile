FROM oracle/graalvm-ce:19.2.0 AS build-aot
MAINTAINER devcsrj

ARG JAR=ispmon-MISSING-fat.jar
ARG JAR_SEC=java.security

COPY $JAR /project/$JAR
COPY $JAR_SEC /project/$JAR_SEC
WORKDIR /project

RUN gu install native-image && native-image \
    --report-unsupported-elements-at-runtime \
    --allow-incomplete-classpath \
    --no-server \
    --static \
    -J-Djava.security.properties=java.security \
    -jar $JAR ispmon

FROM busybox:glibc

COPY --from=build-aot /project/ispmon /opt/ispmon
COPY launcher.sh /opt/launcher.sh

ENV ISPMON_INTERVAL 15

VOLUME /opt/results
EXPOSE 5000

WORKDIR "/opt"
ENTRYPOINT ["sh", "launcher.sh"]
