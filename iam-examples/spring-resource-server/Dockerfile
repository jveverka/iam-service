FROM adoptopenjdk/openjdk11:jre-11.0.7_10-alpine
COPY build/libs/spring-resource-server-1.0.1-SNAPSHOT.jar /spring-resource-server-1.0.1-SNAPSHOT.jar
COPY start-service.sh /start-service.sh
RUN chmod +x /start-service.sh
RUN apk --no-cache add curl
ENTRYPOINT ["/start-service.sh"]
