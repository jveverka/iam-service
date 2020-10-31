FROM adoptopenjdk/openjdk11:jre-11.0.8_10-alpine
COPY build/libs/spring-method-security-1.0.0-SNAPSHOT.jar /spring-method-security-1.0.0-SNAPSHOT.jar
COPY start-service.sh /start-service.sh
RUN chmod +x /start-service.sh
RUN apk --no-cache add curl
ENTRYPOINT ["/start-service.sh"]
