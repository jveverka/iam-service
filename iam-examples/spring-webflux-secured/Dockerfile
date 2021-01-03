FROM adoptopenjdk/openjdk11:alpine-jre
COPY build/libs/spring-webflux-secured-2.4.0-RELEASE.jar /spring-webflux-secured-2.4.0-RELEASE.jar
COPY start-service.sh /start-service.sh
RUN chmod +x /start-service.sh
RUN apk --no-cache add curl
ENTRYPOINT ["/start-service.sh"]
