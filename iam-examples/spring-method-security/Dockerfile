FROM adoptopenjdk/openjdk11:alpine-jre
COPY build/libs/spring-method-security-2.4.1-RELEASE.jar /spring-method-security-2.4.1-RELEASE.jar
COPY start-service.sh /start-service.sh
RUN chmod +x /start-service.sh
RUN apk --no-cache add curl
ENTRYPOINT ["/start-service.sh"]
