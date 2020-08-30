# IAM-service
IAM as microservice. Standalone OAuth2/OpenID-connect authorization and authentication server. 

## Build and Run
```
gradle clean build
java -jar build/libs/iam-service-1.0.0-SNAPSHOT.jar

# run using customized configutation
java -jar build/libs/iam-service-1.0.0-SNAPSHOT.jar --spring.config.location=file:/path/to/application.yml
```

### Build Docker Image 
```
docker build -t iam-service:1.0.0-SNAPSHOT .
docker image list
docker save --output="build/iam-service:1.0.0-SNAPSHOT.tar" iam-service:1.0.0-SNAPSHOT
```

### Run Docker Container
```
docker run -d --name iam-service-1.0.0-SNAPSHOT \
  -e SERVER_PORT=8080 \
  -e ADMIN_PASSWORD=secret \
  -e ADMIN_SECRET=top-secret \
  -e XMX=128m \
  -p 8080:8080 iam-service:1.0.0-SNAPSHOT

docker run -d --name iam-service-1.0.0-SNAPSHOT \
  -e APP_CONFIG_PATH=/opt/data/application.yml \
  -e XMX=128m \
  -v /custom/data/dir:/opt/data \
  -p 8080:8080 iam-service:1.0.0-SNAPSHOT

docker attach iam-service-1.0.0-SNAPSHOT
docker logs iam-service-1.0.0-SNAPSHOT
```
### Docker Cleanup 
```
docker stop iam-service-1.0.0-SNAPSHOT
docker rm iam-service-1.0.0-SNAPSHOT
docker image rm -f iam-service:1.0.0-SNAPSHOT
```