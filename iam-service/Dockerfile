# Compatible with Architecture: x86_64 systems (Intel, AMD)

# https://hub.docker.com/r/adoptopenjdk/openjdk11
FROM adoptopenjdk/openjdk11:alpine-jre
COPY build/libs/iam-service-2.3.0-SNAPSHOT.jar /iam-service-2.3.0-SNAPSHOT.jar
COPY iam-service-start.sh /iam-service-start.sh
RUN chmod +x /iam-service-start.sh
RUN mkdir -p /opt/data
ENV XMX 128m
ENV SERVER_PORT 8080
ENV ADMIN_PASSWORD secret
ENV ADMIN_SECRET top-secret
ENV APP_CONFIG_PATH false
ENTRYPOINT ["/iam-service-start.sh"]
