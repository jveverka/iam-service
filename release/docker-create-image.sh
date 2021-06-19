#!/bin/bash

NOCOLOR='\033[0m'
RED='\033[0;31m'
GREEN='\033[0;32m'

VERSION=2.5.6-RELEASE
DOCKER_IMAGE=jurajveverka/iam-service
DOCKER_NAME=iam-service

ARCH=`lscpu  | grep Architecture | awk '{ print $2 }'`
if [ "${ARCH}" = "x86_64" ]; then
  ARCH=amd64
fi
if [ "${ARCH}" = "aarch64" ]; then
  ARCH=arm64v8
fi

echo "ARCH         : ${ARCH}"
echo "VERSION      : ${VERSION}"
echo "DOCKER_IMAGE : ${DOCKER_IMAGE}"
echo "DOCKER_NAME  : ${DOCKER_NAME}"

if [ -f "iam-service-${VERSION}.jar" ]; then
  echo -e "${GREEN}OK${NOCOLOR}"
else
  echo -e "${RED}ERROR: release file iam-service-${VERSION}.jar not found !${NOCOLOR}"
  echo -e "${RED}Please create release first !${NOCOLOR}"
  exit 1
fi

# stop and delete previous docker image
docker stop ${DOCKER_NAME}-${VERSION}
docker rm ${DOCKER_NAME}-${VERSION}
docker image rm ${DOCKER_IMAGE}:${VERSION}-${ARCH}

cp application.yml application-filesystem.yml
sed -i "s/persistence: in-memory/#persistence: in-memory/g" application-filesystem.yml
sed -i "s/#persistence: file-system/persistence: file-system/g" application-filesystem.yml
sed -i "s/#path: \/path\/to\/model-storage.json/path: \/model-data.json/g" application-filesystem.yml

docker build -t ${DOCKER_IMAGE}:${VERSION}-${ARCH} --file Dockerfile .
if [ $? = 0  ]; then
  echo -e "Docker build ${GREEN}OK${NOCOLOR}"
else
  echo -e "ERROR: ${RED}Docker build has failed !${NOCOLOR}"
  exit 1
fi

docker run -d --restart unless-stopped --name ${DOCKER_NAME}-${VERSION} -p 8080:8080 ${DOCKER_IMAGE}:${VERSION}-${ARCH}
if [ $? = 0  ]; then
  echo -e "Docker container started ${GREEN}OK${NOCOLOR}"
else
  echo -e "ERROR: ${RED}Docker run has failed !${NOCOLOR}"
  exit 1
fi

rm -rf application-filesystem.yml

echo -e "${GREEN}OK${NOCOLOR}"
