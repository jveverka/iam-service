#!/bin/sh

echo "starting iam-service ..."
echo "SERVER_PORT=${SERVER_PORT}"
java -Xms32m -Xms128M -jar /iam-service-1.0.0-SNAPSHOT.jar --server.port=$SERVER_PORT
