FROM --platform=$BUILDPLATFORM gradle:8-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/
WORKDIR /home/gradle/
RUN gradle build --no-daemon

FROM eclipse-temurin:17-jre AS deploy

RUN groupadd -r -g 2000 java && useradd -m -d /home/java/ -s /bin/bash -u 2000 -r -g java java \
    && apt update \
    && apt upgrade -y \
    && echo "wireshark-common wireshark-common/install-setuid boolean true" | debconf-set-selections \
    && DEBIAN_FRONTEND=noninteractive apt install -y tshark \
    && rm -rf /var/lib/apt/lists/* \
    && tshark -v

COPY --from=build /home/gradle/build/libs/*-all.jar /app/uecapabilityparser.jar

USER java
WORKDIR /home/java

ENTRYPOINT [ "java", "-jar", "/app/uecapabilityparser.jar" ]