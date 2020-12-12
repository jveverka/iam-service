#!/bin/sh

echo "starting iam-service ..."

if [ "${APP_CONFIG_PATH}" == "false" ]; then
  echo "using default configuration"
  echo "SERVER_PORT=${SERVER_PORT}"
  echo "XMX=${XMX}"
  java -Xms32m -Xmx${XMX} -jar /iam-service-2.2.0-SNAPSHOT.jar \
     --server.port=${SERVER_PORT} \
     --iam-service.data-model.default-admin-password=${ADMIN_PASSWORD} \
     --iam-service.data-model.default-admin-secret=${ADMIN_SECRET}
else
  echo "using custom configuration"
  echo "APP_CONFIG_PATH=${APP_CONFIG_PATH}"
  echo "XMX=${XMX}"
  java -Xms32m -Xmx${XMX} -jar /iam-service-2.2.0-SNAPSHOT.jar \
     --spring.config.location=file:${APP_CONFIG_PATH}
fi
