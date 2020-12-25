## IAM Server Configuration

* Customize [__application.yml__](../../iam-service/src/main/resources/application.yml) configuration before you start.
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
* Start __iam-service__ as standalone server. You can skip this step if you would like to used iam-service as docker container.
  ```
  java -Xms32m -Xmx128m -jar iam-service-2.4.0-SNAPSHOT.jar --spring.config.location=file:application.yml
  ```
* Build Docker Image locally and run, in case you prefer downloading image from dockerhub please  skip this step.
  Supported platforms are: AMD64  or Intel, ARM32v7 and ARM64v8.
  ```
  ./docker-create-image.sh
  ```
* Use public docker image at dockerhub [jurajveverka/iam-service](https://hub.docker.com/r/jurajveverka/iam-service)
  ```
  docker run -d --name iam-service-2.4.0-SNAPSHOT \
    -e APP_CONFIG_PATH=/opt/iam-service/application.yml \
    -e XMX=128m \
    -v `pwd`:/opt/iam-service \
    -p 8080:8080 jurajveverka/iam-service:2.4.0-SNAPSHOT
  ```
* Check docker status and logs
  ```
  docker ps -a 
  docker exec -ti iam-service-2.4.0-SNAPSHOT /bin/sh
  docker logs --follow iam-service-2.4.0-SNAPSHOT
  ```
* Verify Service state, check OpenAPI documentation.
  ```
  http://localhost:8080/actuator
  http://localhost:8080/v3/api-docs
  http://localhost:8080/swagger-ui/index.html?url=/v3/api-docs
  
  curl 'http://localhost:8080/services/discovery' | json_pp
  curl 'http://localhost:8080/services/oauth2/iam-admins/iam-admins/.well-known/openid-configuration' | json_pp
  ```
* Stop and cleanup Docker
  ```
  docker stop iam-service-2.4.0-SNAPSHOT
  docker rm iam-service-2.4.0-SNAPSHOT
  docker image rm -f iam-service:2.4.0-SNAPSHOT
  ```

* [next step: Setup Persistence Layer](01b_setup-persitence-layer.md)
* [Terms and Vocabulary](Terms-and-Vocabulary.md)