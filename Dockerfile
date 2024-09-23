FROM --platform=$BUILDPLATFORM gradle:8.8-jdk11-jammy AS build

COPY --chown=gradle:gradle . /home/gradle/
WORKDIR /home/gradle/
# Replace project.version=staging with project.version=commit@hash
RUN git config --global --add safe.directory "*" \
    && rev=$(git rev-parse --short --verify HEAD) \
    && sed -i "s/\(project.version=\)staging$/\1commit@$rev/g" src/main/resources/application.properties \
    || true
# Build jar
RUN gradle build --no-daemon


FROM eclipse-temurin:21-jre-noble AS deploy

ARG SCAT_TAG=2a76b80
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PATH="${PATH}:/scat/bin"

RUN groupadd -r -g 2000 java && useradd -m -d /home/java/ -s /bin/bash -u 2000 -r -g java java \
    && apt-get update \
    && apt-get upgrade -y \
    && apt-get install -y tshark python3 python3-venv --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /home/gradle/build/libs/*-all.jar /app/uecapabilityparser.jar

# Install scat
RUN python3 -m venv /scat \
    && /scat/bin/python -m pip install --no-cache-dir https://github.com/fgsect/scat/archive/${SCAT_TAG}.tar.gz

USER java
WORKDIR /home/java
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70.0 -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC -XX:ShenandoahUncommitDelay=30000 -XX:ShenandoahGuaranteedGCInterval=60000"

ENTRYPOINT [ "java", "-jar", "/app/uecapabilityparser.jar" ]
