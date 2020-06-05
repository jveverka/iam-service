# IAM-service
IAM as microservice. Standalone OAuth2/OpenID-connect authorization and authentication server. 

## Build and Run
```
gradle clean build
java -jar build/libs/iam-service-1.0.0-SNAPSHOT.jar
```

### Build Docker image 
```
docker build . -t iam-service:1.0.0-SNAPSHOT
docker image list
docker save --output="build/iam-service:1.0.0-SNAPSHOT.tar" iam-service:1.0.0-SNAPSHOT
docker image rm -f <imageid>
docker run -p 8880:8080 iam-service:1.0.0-SNAPSHOT
```