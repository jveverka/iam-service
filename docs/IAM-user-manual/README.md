# IAM Service User's Guide
This guide shows how to setup really simple standalone *Identity Access Management* (IAM) service, 
authentication and authorization OAuth2 server.

## 1. OAuth2 Server setup
* [Use docker image](https://hub.docker.com/r/jurajveverka/iam-service) or
  [Download](https://github.com/jveverka/iam-service/releases/) latest release.
* [Customize __application.yml__ configuration and start IAM server](01a-standalone-server-config.md).
* [Setup persistence Layer](01b_setup-persitence-layer.md) and [Caching](01c_setup-cache-layer.md).

## 2. OAuth2 Server administration
* [Getting Admin Access](02a-get-admin-access-token.md).
* [Create new Organizations and Projects](02b-create-organization-with-admin.md).
* [Manage your new Organization and Projects](02c-manage-organization-and-projects.md).
* [Get Access_Tokens for new users](02d-getting-access-tokens-for-new-users.md).

### Supported OAuth2 flows
* [x] __Authorization Code__ - [flow details](../oauth2/131_authorization-code-flow.md).
* [x] __Authorization Code (With PKCE)__ - [flow details](../oauth2/131_authorization-code-flow.md).
* [x] __Password Credentials__ - [flow details](../oauth2/133_password-credentials-flow.md).
* [x] __Client Credentials__ - [flow details](../oauth2/134_client-credentials-flow.md).
* [x] __Refresh Token__ - [flow detail](../oauth2/15_refresh-tokens-flow.md).

### Examples and Client Libraries
* [Examples](../../iam-examples) - how to consume iam-service in your microservices.
* [IAM-Client for java](../../iam-common/iam-client) - remote iam-client.
* [IAM-Service-Client for java](../../iam-common/iam-service-client) - remote admin client.

### Advanced Configuration
* [Running IAM-Service behind proxy](Advanced-Config-Running-Behind-Proxy.md)

### References
* [Terms and Vocabulary](Terms-and-Vocabulary.md)
* [Security Model](IAM-Service-Security-Model.md)
