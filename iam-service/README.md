# IAM-service
IAM as microservice. Standalone OAuth2/OpenID-connect authorization and authentication server. 

## Build and Run
```
gradle clean build
java -jar build/libs/iam-service-2.4.0-RELEASE.jar

# run using customized configutation
java -jar build/libs/iam-service-2.4.0-RELEASE.jar --spring.config.location=file:/path/to/application.yml
```

### Build Docker Image 
```
docker build -t iam-service:2.4.0-RELEASE .
docker image list
docker save --output="build/iam-service:2.4.0-RELEASE.tar" iam-service:2.4.0-RELEASE
```

### Run Docker Container
```
docker run -d --name iam-service-2.4.0-RELEASE \
  -e SERVER_PORT=8080 \
  -e ADMIN_PASSWORD=secret \
  -e ADMIN_SECRET=top-secret \
  -e XMX=128m \
  -p 8080:8080 iam-service:2.4.0-RELEASE

docker run -d --name iam-service-2.4.0-RELEASE \
  -e APP_CONFIG_PATH=/opt/data/application.yml \
  -e XMX=128m \
  -v /custom/data/dir:/opt/data \
  -p 8080:8080 iam-service:2.4.0-RELEASE

docker attach iam-service-2.4.0-RELEASE
docker logs iam-service-2.4.0-RELEASE
```
### Docker Cleanup 
```
docker stop iam-service-2.4.0-RELEASE
docker rm iam-service-2.4.0-RELEASE
docker image rm -f iam-service:2.4.0-RELEASE
```