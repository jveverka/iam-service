# IAM Service 2.5.9-RELEASE
Really simple standalone OAuth2/OIDC
[*Identity Access Management* (IAM) service](https://github.com/jveverka/iam-service/tree/2.5.9-RELEASE), 
authentication and authorization server. 

## Quick startup guide
* Modify __application.yml__ if necessary, 
  see [user's manual](https://github.com/jveverka/iam-service/tree/2.x.x/docs/IAM-user-manual/README.md). 
* Start MongoDB and Redis as docker containers only if necessary.
  ```
  docker-compose up -d
  ``` 
* Start __iam-service__ (Requires JVM installed).
  ```
  java -Xms32m -Xmx128m -jar iam-service-2.5.9-RELEASE.jar --spring.config.location=file:application.yml
  ```
* Start __iam-service__ as docker container.
  ```
  docker run -d --name iam-service-2.5.9-RELEASE \
     -p 8080:8080 jurajveverka/iam-service:2.5.9-RELEASE
  ```

### Next Steps
* Check [user's manual](https://github.com/jveverka/iam-service/tree/2.x.x/docs/IAM-user-manual/README.md) for the next steps:
* See [examples how to integrate](https://github.com/jveverka/iam-service/tree/2.x.x/iam-examples) __iam-service__ in microservice environment.

### Cleanup
* Remove MongoDB and Redis docker containers.
  ```
  docker-compose down -v --rmi all --remove-orphans
  ```     