[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/jveverka/iam-service.svg?branch=master)](https://travis-ci.org/jveverka/iam-service)

# IAM service
Really simple standalone Identity Access Management (IAM) service, authentication and authorization server. Project is compliant with subset of OAuth2 and OpenID-connect specifications. Issued tokens comply with JWT.

This project is __WIP__, stay tuned !

## Related RFCs and Specifications
* [RFC6749](https://tools.ietf.org/html/rfc6749) - The OAuth 2.0 Authorization Framework
* [RFC7519](https://tools.ietf.org/html/rfc7519) - JSON Web Token (JWT)
* [OpenID](https://openid.net/specs/openid-connect-core-1_0.html) - OpenID Connect Core 1.0

## Architecture
![architecture](docs/IAM-service-architecture.svg)

### Components
* __iam-core__ - core implementation of IAM business logic (no framework dependencies).
* __iam-service__ - [SpringBoot](https://spring.io/projects/spring-boot) IAM as microservice. 

## Data model
![data-model](docs/IAM-data-model.svg)

### JWT mappings
Data model is mapped to registered [JWT claim names](https://tools.ietf.org/html/rfc7519#section-4) according rules below:
* __iss__ (issuer) - OrganizationId, string. 
* __aud__  (audience) - ProjectId, string.
* __subj__ (subject) - ClientId, string.

Remaining registered [JWT claim names](https://tools.ietf.org/html/rfc7519#section-4) are used as following:
* __exp__ (Expiration Time) = iat + session duration, datetime+timezone, string
* __nbf__ (Not Before) = iat, datetime+timezone, string
* __iat__ (Issued At) = current datetime+timezone, string
* __jti__ (JWT ID) - unique id, random uuid string.

Non-registered mappings:
* __roles__ - string array of roleId(s) for subject. 

## Supported OAuth2 flows
* __Password Credentials__ - [RFC reference](https://tools.ietf.org/html/rfc6749#section-1.3.3), [details](docs/oauth2/003_password-credentials-flow.md).
* __Client Credentials__ - [RFC reference](https://tools.ietf.org/html/rfc6749#section-1.3.4), [details](docs/oauth2/004_client-credentials-flow.md).

## Build and Run
```
gradle clean build test
java -jar iam-service/build/libs/iam-service-1.0.0-SNAPSHOT.jar
```


