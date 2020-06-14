#!/bin/bash

#1. Build project and run JUnit tests
gradle clean build test

#2. Create and deploy docker images
docker-compose up --build -d

#3. Wait for all REST services to start.
until $(curl --silent --output /dev/null -f http://127.0.0.1:8080/services/authentication/iam-admins/iam-admins/.well-known/jwks.json ); do
  echo "Waiting for iam-service to start ..."
  sleep 1
done
until [ $(curl --silent --output /dev/null -f http://127.0.0.1:8082/services/info -w '%{http_code}\n')  -eq  "200" ]; do
  echo "Waiting for spring-method-security to start ..."
  sleep 1
done
until [ $(curl --silent --output /dev/null -f http://127.0.0.1:8081/services/info -w '%{http_code}\n')  -eq  "401" ]; do
  echo "Waiting for spring-resource-server to start ..."
  sleep 1
done

#4. Run Integration tests for 'spring-method-security' demo
gradle :spring-method-security:clean :spring-method-security:test -Dtest.profile=integration
#5. Run Integration tests for 'spring-resource-server' demo
gradle :spring-resource-server:clean :spring-resource-server:test -Dtest.profile=integration

#6. Shutdown and Cleanup docker
docker-compose down -v --rmi all --remove-orphans
