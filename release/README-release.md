# IAM Service 1.3.0-RELEASE
Really simple standalone OAuth2/OIDC
[*Identity Access Management* (IAM) service](https://github.com/jveverka/iam-service/tree/1.3.0-RELEASE), 
authentication and authorization server. 

## TL;DR - The quick startup guide
* Modify __application.yml__ if necessary.
* Start MongoDB and Redis as docker containers if necessary.
  ```
  docker-compose up -d
  ``` 
* Start __iam-service__.
  ```
  java -Xms32m -Xmx128m -jar iam-service-1.3.0-RELEASE.jar --spring.config.location=file:application.yml
  ```

### Next Steps
* Check [user's manual](https://github.com/jveverka/iam-service/tree/1.3.0-RELEASE/docs/IAM-user-manual/README.md) for the next steps:
  * How to use, install and configure __iam-service__.
  * How to create/manage organizations and projects.
  * How to create/manage and manage clients and users.
  * How  to assign roles and permissions to clients and users.
* See [examples how to integrate](https://github.com/jveverka/iam-service/tree/1.3.0-RELEASE/iam-examples) __iam-service__ in microservice environment.

### Cleanup
* Remove MongoDB and Redis docker containers.
  ```
  docker-compose down -v --rmi all --remove-orphans
  ```     