FROM adoptopenjdk/openjdk11:jre-11.0.8_10-alpine
COPY build/libs/iam-service-1.0.0-SNAPSHOT.jar /iam-service-1.0.0-SNAPSHOT.jar
COPY iam-service-start.sh /iam-service-start.sh
RUN chmod +x /iam-service-start.sh
RUN mkdir -p /opt/data
ENV XMX 128m
ENV SERVER_PORT 8080
ENV ADMIN_PASSWORD secret
ENV ADMIN_SECRET top-secret
ENV APP_CONFIG_PATH false
ENTRYPOINT ["/iam-service-start.sh"]
