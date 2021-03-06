FROM adoptopenjdk/openjdk11:alpine-jre
COPY build/libs/spring-resource-server-2.5.2-RELEASE.jar /spring-resource-server-2.5.2-RELEASE.jar
COPY start-service.sh /start-service.sh
RUN chmod +x /start-service.sh
RUN apk --no-cache add curl
ENTRYPOINT ["/start-service.sh"]
