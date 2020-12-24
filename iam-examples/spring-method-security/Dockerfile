FROM adoptopenjdk/openjdk11:alpine-jre
COPY build/libs/spring-method-security-2.4.0-SNAPSHOT.jar /spring-method-security-2.4.0-SNAPSHOT.jar
COPY start-service.sh /start-service.sh
RUN chmod +x /start-service.sh
RUN apk --no-cache add curl
ENTRYPOINT ["/start-service.sh"]
