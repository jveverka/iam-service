# Spring Method Security Demo

## Build and Run
```
gradle clean build
java -jar build/libs/spring-method-security-1.0.0-SNAPSHOT.jar
```

### Build Docker image 
```
docker build . -t spring-method-security:1.0.0-SNAPSHOT
docker image list
docker save --output="build/spring-method-security:1.0.0-SNAPSHOT.tar" spring-method-security:1.0.0-SNAPSHOT
docker image rm -f <imageid>
docker run -p 8082:8082 spring-method-security:1.0.0-SNAPSHOT
```
