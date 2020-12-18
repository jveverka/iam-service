#!/bin/sh

echo "Starting spring-webflux-secured"

until $(curl --silent --output /dev/null -f http://127.0.0.1:8080/services/oauth2/iam-admins/iam-admins/.well-known/jwks.json ); do
  echo "Waiting for iam-service to start ..."
  sleep 1
done

java -Xms32m -Xmx128M -jar /spring-webflux-secured-2.3.0-SNAPSHOT.jar
