#!/bin/sh

echo "starting iam-service ..."
echo "SERVER_PORT=${SERVER_PORT}"
java -Xms32m -Xms128M -jar /iam-service-1.0.0-SNAPSHOT.jar \
     --server.port=${SERVER_PORT} \
     --iam-service.data-model.default-admin-password=${ADMIN_PASSWORD} \
     --iam-service.data-model.default-admin-secret=${ADMIN_SECRET}
