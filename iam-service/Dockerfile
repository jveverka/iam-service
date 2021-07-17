ARG ARCH="amd64"
FROM ${ARCH}/adoptopenjdk:11-jre
COPY build/libs/iam-service-2.5.7-RELEASE.jar /iam-service-2.5.7-RELEASE.jar
COPY iam-service-start.sh /iam-service-start.sh
COPY src/main/resources/application.yml /application.yml
COPY src/main/resources/application-cloud.yml /application-cloud.yml
RUN chmod +x /iam-service-start.sh
RUN mkdir -p /opt/data
ENV XMX 128m
ENV SERVER_PORT 8080
ENV ADMIN_PASSWORD secret
ENV ADMIN_SECRET top-secret
ENV APP_CONFIG_PATH false
ENV JVM_OPTS ""
ENTRYPOINT ["/iam-service-start.sh"]
