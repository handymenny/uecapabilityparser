FROM --platform=$BUILDPLATFORM gradle:8-jdk11-jammy AS build

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1
# update and install pip/venv
RUN apt update \
    && apt upgrade -y \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y python3-pip  python3-venv --no-install-recommends
# Install scat
RUN python3 -m venv /scat \
    && /scat/bin/python -m pip install git+https://github.com/fgsect/scat

COPY --chown=gradle:gradle . /home/gradle/
WORKDIR /home/gradle/
# Replace project.version=staging with project.version=commit@hash
RUN git config --global --add safe.directory "*" \
    && rev=$(git rev-parse --short --verify HEAD) \
    && sed -i "s/\(project.version=\)staging$/\1commit@$rev/g" src/main/resources/application.properties \
    || true
# Build jar
RUN gradle build --no-daemon


FROM eclipse-temurin:17-jre-jammy AS deploy

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PATH="${PATH}:/scat/bin"

RUN groupadd -r -g 2000 java && useradd -m -d /home/java/ -s /bin/bash -u 2000 -r -g java java \
    && apt update \
    && apt upgrade -y \
    && echo "wireshark-common wireshark-common/install-setuid boolean true" | debconf-set-selections \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y tshark python3 --no-install-recommends \
    && rm -rf /var/lib/apt/lists/* \
    && tshark -v

COPY --from=build /home/gradle/build/libs/*-all.jar /app/uecapabilityparser.jar
COPY --from=build /scat /scat

USER java
WORKDIR /home/java
ENV JAVA_TOOL_OPTIONS -XX:MaxRAMPercentage=70.0 -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC -XX:ShenandoahUncommitDelay=30000 -XX:ShenandoahGuaranteedGCInterval=60000

ENTRYPOINT [ "java", "-jar", "/app/uecapabilityparser.jar" ]
