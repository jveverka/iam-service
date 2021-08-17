ARG ARCH="amd64"
FROM ${ARCH}/adoptopenjdk:11-jre
MAINTAINER Juraj Veverka <gergej123@gmail.com>
COPY iam-service-2.5.9-RELEASE.jar /iam-service-2.5.9-RELEASE.jar
COPY iam-service-start.sh /iam-service-start.sh
COPY application-filesystem.yml /application.yml
COPY application-cloud.yml /application-cloud.yml
RUN chmod +x /iam-service-start.sh
RUN mkdir -p /opt/data

RUN addgroup iamuser
RUN useradd -g iamuser -m iamuser
RUN chown -R iamuser:iamuser /opt/data
USER iamuser

ENV XMX 128m
ENV SERVER_PORT 8080
ENV ADMIN_PASSWORD secret
ENV ADMIN_SECRET top-secret
ENV APP_CONFIG_PATH "/application.yml"
ENV JVM_OPTS ""
ENTRYPOINT ["/iam-service-start.sh"]
