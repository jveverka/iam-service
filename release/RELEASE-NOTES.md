# IAM service - OAuth2 server
Simple standalone OAuth2/OIDC  [*Identity Access Management* (IAM) service](https://github.com/jveverka/iam-service/tree/v2.5.6-RELEASE), authentication and authorization server.

## Quick startup guide
1. Start OAuth2 server in default configuration [dockerhub project](https://hub.docker.com/r/jurajveverka/iam-service).
   ```
   docker run --name iam-service-2.5.6-RELEASE \
      --restart unless-stopped \
      -d -p 8080:8080 jurajveverka/iam-service:2.5.6-RELEASE
   curl 'http://localhost:8080/services/discovery' | json_pp
   curl 'http://localhost:8080/services/oauth2/iam-admins/iam-admins/.well-known/openid-configuration' | json_pp
   ```
2. OpenAPI/Swagger documentation:
   ```
   http://localhost:8080/swagger-ui/index.html?url=/v3/api-docs#/
   ```
## Advanced configurations
1. Download release file ``iam-service-release-2.5.6-RELEASE.zip``
2. Please check [__User's Guide__](https://github.com/jveverka/iam-service/tree/2.x.x/docs/IAM-user-manual) and
   [__Examples__](https://github.com/jveverka/iam-service/tree/2.x.x/iam-examples) for more details.
