# IAM User's Guide
This guide shows how to setup really simple standalone *Identity Access Management* (IAM) service, 
authentication and authorization OAuth2 server.

## 1. OAuth2 Server setup
* [Download](https://github.com/jveverka/iam-service/releases/) latest release, Follow release instructions. 
* Start iam-service as standalone server.
* Start iam-service as docker container.

## 2. OAuth2 Server administration
* Get Admin access_token.
* Create organization with Admin user.
* Manage your Organization / Projects.

## 3. Self-service features for projects
* Users self-registration using email.
* Change user's credentials.

### Supported OAuth2 flows
* [x] __Authorization Code__ - [flow details](oauth2/131_authorization-code-flow.md).
* [x] __Password Credentials__ - [flow details](oauth2/133_password-credentials-flow.md).
* [x] __Client Credentials__ - [flow details](oauth2/134_client-credentials-flow.md).
* [x] __Refresh Token__ - [flow detail](oauth2/15_refresh-token.md).

### Integration Examples
* [Spring method security example](../iam-examples/spring-method-security)
* [Spring resource server example](../iam-examples/spring-resource-server)
* [IAM-Client java library](../iam-common/iam-client)
