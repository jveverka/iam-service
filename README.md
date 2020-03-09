[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/jveverka/iam-service.svg?branch=master)](https://travis-ci.org/jveverka/iam-service)

# IAM service
Really simple Identity Access Management (IAM) service, authentication and authorization server.

This project is __WIP__ !

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

## Build and Run
```
gradle clean build test
```


