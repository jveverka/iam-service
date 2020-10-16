# IAM Service
Really simple standalone OAuth2/OIDC
[*Identity Access Management* (IAM) service](https://github.com/jveverka/iam-service), 
authentication and authorization server. 

* Start as java process.
  ```
  java -Xms32m -Xmx128m -jar iam-service-1.0.0-SNAPSHOT.jar --spring.config.location=file:application.yml
  ```
* Build Docker Image for target platform.
  ```
  docker build -t iam-service:1.0.0-SNAPSHOT --file Dockerfile.x86_64 .
  docker build -t iam-service:1.0.0-SNAPSHOT --file Dockerfile.armv7l .
  docker build -t iam-service:1.0.0-SNAPSHOT --file Dockerfile.aarch64 .
  ```
* Start Docker Container - custom configuration.
  ```
  docker run -d --name iam-service-1.0.0-SNAPSHOT \
    -e APP_CONFIG_PATH=/opt/data/application.yml \
    -e XMX=128m \
    -v `pwd`:/opt/data \
    -p 8080:8080 iam-service:1.0.0-SNAPSHOT  
  
  docker attach iam-service-1.0.0-SNAPSHOT
  docker logs iam-service-1.0.0-SNAPSHOT
  ```
* Verify service state.
  ```
  curl http://localhost:8080/actuator
  curl http://localhost:8080/v3/api-docs
  curl http://localhost:8080/swagger-ui/index.html?url=/v3/api-docs
  ```  
* Check [user's manual](https://github.com/jveverka/iam-service/blob/master/docs/IAM-users-manual.md) for next steps:
  * How to create/manage organizations and projects.
  * How to create/manage and manage clients and users.
  * How to integrate __iam-service__ in microservice environment.
  * How to modify __application.yml__ configuration file.

* Cleanup Docker
  ```
  docker stop iam-service-1.0.0-SNAPSHOT
  docker rm iam-service-1.0.0-SNAPSHOT
  docker image rm -f iam-service:1.0.0-SNAPSHOT
  ```
  