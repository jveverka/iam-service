## IAM Server Configuration

* Customize [__application.yml__](../../iam-service/src/main/resources/application.yml) configuration before you start.
  Default configuration is fully operational, however it is recommended 
  to override default admin user's password, email and admin-client secret.
  ```
  iam-service:
    data-model:
      default-admin-password: s3Cr3T
      default-admin-client-secret: S3cR3t 
      default-admin-email: admin@email.com
      enable-client-credentials-flow: true 
  ```
* Start __iam-service__ as standalone server. You can skip this step if you would like to use *iam-service* in docker container.
  ```
  java -Xms32m -Xmx128m -jar iam-service-2.5.5-RELEASE.jar \
    --spring.config.location=file:application.yml
  ```
* Build Docker Image locally and run, in case you prefer downloading image from dockerhub please  skip this step.
  Supported platforms are: AMD64  or Intel, ARM32v7 and ARM64v8.
  ```
  ./docker-create-image.sh
  ```
* Use public docker dockerhub image [jurajveverka/iam-service](https://hub.docker.com/r/jurajveverka/iam-service)
  ```
  docker run -d --name iam-service-2.5.5-RELEASE \
    --restart unless-stopped \
    -e APP_CONFIG_PATH=/opt/data/application.yml \
    -e XMX=128m \
    -v '${IAM_DATA_DIR}':/opt/data \
    -p 8080:8080 jurajveverka/iam-service:2.5.5-RELEASE-amd64
  ```
  ``IAM_DATA_DIR`` points to directory where customized ``application.yml`` file is located. 
* Check docker status and logs
  ```
  docker ps -a 
  docker exec -ti iam-service-2.5.5-RELEASE /bin/sh
  docker logs --follow iam-service-2.5.5-RELEASE
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
  docker stop iam-service-2.5.5-RELEASE
  docker rm iam-service-2.5.5-RELEASE
  docker image rm -f iam-service:2.5.5-RELEASE
  ```

* [next step: Setup Persistence Layer](01b_setup-persitence-layer.md)
* [Terms and Vocabulary](Terms-and-Vocabulary.md)