FROM adoptopenjdk/openjdk11:jre-11.0.7_10-alpine
COPY build/libs/iam-service-1.0.0-SNAPSHOT.jar /iam-service-1.0.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-Xms32m", "-Xms128M", "-jar","/iam-service-1.0.0-SNAPSHOT.jar"]
