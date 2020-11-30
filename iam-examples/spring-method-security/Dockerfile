FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine
COPY build/libs/spring-method-security-1.3.0-RELEASE.jar /spring-method-security-1.3.0-RELEASE.jar
COPY start-service.sh /start-service.sh
RUN chmod +x /start-service.sh
RUN apk --no-cache add curl
ENTRYPOINT ["/start-service.sh"]
