FROM ubuntu
MAINTAINER devcsrj

RUN apt-get update && \
    apt-get -y install gcc libc6-dev zlib1g-dev curl bash && \
    rm -rf /var/lib/apt/lists/*

# Latest version of GraalVM (at the time of writing)
ENV GRAAL_VERSION 19.2.0
ENV GRAAL_FILENAME graalvm-ce-linux-amd64-${GRAAL_VERSION}.tar.gz

# Download GraalVM
RUN curl -4 -L https://github.com/oracle/graal/releases/download/vm-${GRAAL_VERSION}/${GRAAL_FILENAME} -o /tmp/${GRAAL_FILENAME}

# Untar and move the files we need:
RUN tar -zxvf /tmp/${GRAAL_FILENAME} -C /tmp \
    && mv /tmp/graalvm-ce-${GRAAL_VERSION} /usr/lib/graalvm \
    && /usr/lib/graalvm/bin/gu install native-image

RUN rm -rf /tmp/*

# Create a volume to which we can mount to build:
VOLUME /project
WORKDIR /project

# And finally, run native-image
ENTRYPOINT ["/usr/lib/graalvm/bin/native-image"]
