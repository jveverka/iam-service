# IAM User's Manual
This chapter shows how to setup iam-service as OAuth2 server.

## OAuth2 Server setup
* [Download](https://github.com/jveverka/iam-service/releases/) latest release, Follow release instructions. 
* Start iam-service as standalone server.
* Start iam-service as docker container.

## OAuth2 Server Admin guidelines
* Get Admin access_token.
* Create organization with Admin user.
* Manage your Organization / Projects.

## Self-service features for projects
* Users self-registration using email.
* Change user's credentials.

## Supported OAuth2 flows
* [x] __Authorization Code__ - [flow details](oauth2/131_authorization-code-flow.md).
* [x] __Password Credentials__ - [flow details](oauth2/133_password-credentials-flow.md).
* [x] __Client Credentials__ - [flow details](oauth2/134_client-credentials-flow.md).
* [x] __Refresh Token__ - [flow detail](oauth2/15_refresh-token.md).

## Integration Examples
* [Spring method security example](../iam-examples/spring-method-security)
* [Spring resource server example](../iam-examples/spring-resource-server)
* [IAM-Client java library](../iam-common/iam-client)
