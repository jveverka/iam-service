# IAM-service
IAM as microservice. Standalone OAuth2/OpenID-connect authorization and authentication server. 

## Build and Run
```
gradle clean build
java -jar build/libs/iam-service-1.0.0-SNAPSHOT.jar

# run using customized configutation
java -jar build/libs/iam-service-1.0.0-SNAPSHOT.jar --spring.config.location=file:/path/to/application.yml
```

### Build Docker image 
```
docker build . -t iam-service:1.0.0-SNAPSHOT
docker image list
docker save --output="build/iam-service:1.0.0-SNAPSHOT.tar" iam-service:1.0.0-SNAPSHOT
docker image rm -f iam-service:1.0.0-SNAPSHOT
docker run -p 8080:8080 iam-service:1.0.0-SNAPSHOT
```