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

## Build and Run
```
gradle clean build test
```


