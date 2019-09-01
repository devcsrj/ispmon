FROM vertx/vertx3

ARG VERTICLE_NAME=com.github.devcsrj.ispmon.Server
ARG VERTICLE_FILE=build/libs/ispmon-fat.jar

ENV VERTICLE_NAME $VERTICLE_NAME
ENV VERTICLE_FILE $VERTICLE_FILE

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your verticle to the container                   (2)
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
