#!/bin/sh

echo "Starting spring-method-security"

until $(curl --silent --output /dev/null -f http://127.0.0.1:8080/services/health/status); do
  echo "Waiting for iam-service to start ..."
  sleep 1
done

java -Xms32m -Xms128M -jar /spring-method-security-1.0.0-SNAPSHOT.jar
