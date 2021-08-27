#!/bin/bash

echo "starting iam-service as ${USER} ..."
echo "APP_CONFIG_PATH=${APP_CONFIG_PATH}"

if [ "${IAM_PERSISTENCE_TYPE}" == "mongo-db" ]; then
   echo "Waiting for MongoDB to launch on ${IAM_MONGO_PERSISTENCE_HOST}:${IAM_MONGO_PERSISTENCE_PORT} ..."
   while ! nc -z ${IAM_MONGO_PERSISTENCE_HOST} ${IAM_MONGO_PERSISTENCE_PORT}; do
     sleep 0.5 # wait for 0.5s before check again
     echo -n "."
   done
   echo ""
   echo "MongoDB launched !"
fi

if [ "${APP_CONFIG_PATH}" = "false" ]; then
  echo "using default configuration"
  echo "SERVER_PORT=${SERVER_PORT}"
  echo "XMX=${XMX}"
  java -Xms32m -Xmx${XMX} ${JVM_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /iam-service-2.5.10-RELEASE.jar \
     --server.port=${SERVER_PORT} \
     --iam-service.data-model.default-admin-password=${ADMIN_PASSWORD} \
     --iam-service.data-model.default-admin-secret=${ADMIN_SECRET}
else
  echo "using custom configuration"
  echo "APP_CONFIG_PATH=${APP_CONFIG_PATH}"
  echo "XMX=${XMX}"
  java -Xms32m -Xmx${XMX} ${JVM_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /iam-service-2.5.10-RELEASE.jar \
     --spring.config.location=file:${APP_CONFIG_PATH}
fi
