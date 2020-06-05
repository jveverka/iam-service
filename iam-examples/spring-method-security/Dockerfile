#FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
COPY build/libs/spring-method-security-1.0.0-SNAPSHOT.jar /spring-method-security-1.0.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-Xms32m", "-Xms128M", "-jar","/spring-method-security-1.0.0-SNAPSHOT.jar"]
