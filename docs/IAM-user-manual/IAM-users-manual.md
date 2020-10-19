# IAM User's Guide
This guide shows how to setup really simple standalone *Identity Access Management* (IAM) service, 
authentication and authorization OAuth2 server.

## 1. OAuth2 Server setup
* [Download](https://github.com/jveverka/iam-service/releases/) latest release, Follow release instructions. 
* [Customize __application.yml__ configuration and start IAM server](01a-standalone-server-config.md).

## 2. OAuth2 Server administration
* [Get Admin access_token](02a-get-admin-access-token.md).
* [Create new organization / project with Admin user](02b-create-organization-with-admin.md).
* [Manage your new Organization / Projects](02c-manage-organization-and-projects.md).

## 3. Self-service features for projects
* [ ] TODO: Users self-registration using email.
* [ ] TODO: Change user's credentials.

### Supported OAuth2 flows
* [x] __Authorization Code__ - [flow details](../oauth2/131_authorization-code-flow.md).
* [x] __Password Credentials__ - [flow details](../oauth2/133_password-credentials-flow.md).
* [x] __Client Credentials__ - [flow details](../oauth2/134_client-credentials-flow.md).
* [x] __Refresh Token__ - [flow detail](../oauth2/15_refresh-token.md).

### Integration Examples
* [Spring method security example](../../iam-examples/spring-method-security)
* [Spring resource server example](../../iam-examples/spring-resource-server)
* [IAM-Client java library](../../iam-common/iam-client)
