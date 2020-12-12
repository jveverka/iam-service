FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine
COPY build/libs/spring-webflux-secured-2.2.0-SNAPSHOT.jar /spring-webflux-secured-2.2.0-SNAPSHOT.jar
COPY start-service.sh /start-service.sh
RUN chmod +x /start-service.sh
RUN apk --no-cache add curl
ENTRYPOINT ["/start-service.sh"]
