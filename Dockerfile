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

FROM alpine:3.10

RUN apk add --no-cache bash
COPY --from=build-aot /project/ispmon /opt/ispmon
COPY --from=build-aot /opt/graalvm-ce-19.2.0/jre/lib/amd64/libsunec.so /libsunec.so
COPY --from=build-aot /opt/graalvm-ce-19.2.0/jre/lib/security/cacerts /cacerts

EXPOSE 5000

ENTRYPOINT ["/opt/ispmon"]
