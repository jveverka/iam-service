## IAM Server Configuration

* Create custom __application.yml__ configuration before you start.
  Default configuration is fully operational, however it is recommended 
  to override default admin user's password, email and client secret.
  ```
  iam-service:
    data-model:
      default-admin-password: s3Cr3T
      default-admin-client-secret: S3cR3t 
      default-admin-email: admin@email.com
      enable-client-credentials-flow: true 
  ```
* Start __iam-service__ as standalone server.
  ```
  java -Xms32m -Xmx128m -jar iam-service-2.2.0-SNAPSHOT.jar --spring.config.location=file:application.yml
  ```
* Build Docker Image for target platform.
  ```
  docker build -t iam-service:2.2.0-SNAPSHOT --file Dockerfile.x86_64 .
  docker build -t iam-service:2.2.0-SNAPSHOT --file Dockerfile.armv7l .
  docker build -t iam-service:2.2.0-SNAPSHOT --file Dockerfile.aarch64 .
  ```
* Start __iam-service__ as Docker Container with custom configuration.
  ```
  docker run -d --name iam-service-2.2.0-SNAPSHOT \
    -e APP_CONFIG_PATH=/opt/iam-service/application.yml \
    -e XMX=128m \
    -v `pwd`:/opt/iam-service \
    -p 8080:8080 iam-service:2.2.0-SNAPSHOT  
  
  docker attach iam-service-2.2.0-SNAPSHOT
  docker logs iam-service-2.2.0-SNAPSHOT
  ```
* Verify Service state, check OpenAPI documentation.
  ```
  http://localhost:8080/actuator
  http://localhost:8080/v3/api-docs
  http://localhost:8080/swagger-ui/index.html?url=/v3/api-docs
  ```
* Stop and cleanup Docker
  ```
  docker stop iam-service-2.2.0-SNAPSHOT
  docker rm iam-service-2.2.0-SNAPSHOT
  docker image rm -f iam-service:2.2.0-SNAPSHOT
  ```

* [next step: Setup Persistence Layer](01b_setup-persitence-layer.md)
* [Terms and Vocabulary](Terms-and-Vocabulary.md)