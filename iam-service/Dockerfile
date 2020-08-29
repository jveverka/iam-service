FROM adoptopenjdk/openjdk11:jre-11.0.7_10-alpine
COPY build/libs/iam-service-1.0.0-SNAPSHOT.jar /iam-service-1.0.0-SNAPSHOT.jar
COPY iam-servivce-start.sh /iam-servivce-start.sh
RUN chmod +x /iam-servivce-start.sh
ENV SERVER_PORT 8080
ENTRYPOINT ["/iam-servivce-start.sh"]
