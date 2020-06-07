# Spring Method Security Demo
This __iam-service__ integration demo utilizes [Spring Security Framework](https://docs.spring.io/spring-security/site/docs/5.3.3.BUILD-SNAPSHOT/reference/html5/#preface), 
particularly [method security](https://www.baeldung.com/spring-security-method-security). 
JWT tokens issues by __iam-service__ are used to get access to resources hosted by __spring-method-security__ service. 

![demo-architecture](docs/spring-method-security.svg)

1. spring-method-security downloads the list of JWKs over http as specified in RFC7517 on startup.
2. __iam-service__ issues token to client application using one of supported OAuth2 flows.
3. __client application__ uses issued JWT token to access resources hosted on __spring-method-security__. 

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
