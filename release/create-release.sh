#!/bin/bash

NOCOLOR='\033[0m'
RED='\033[0;31m'
GREEN='\033[0;32m'

cd ..
./run-full-test-suite.sh
if [ $? = 0  ]; then
  echo -e "Build ${GREEN}OK${NOCOLOR}"
else
  echo -e "ERROR: ${RED}build failed !${NOCOLOR}"
  exit 1
fi

HOME_DIR=`pwd`
VERSION=2.3.0-SNAPSHOT
RELEASE_DIR=build/iam-service-${VERSION}

cd ${HOME_DIR}
rm -rf ${RELEASE_DIR}/iam-service-release-${VERSION}.zip
rm -rf ${RELEASE_DIR}
mkdir -p ${RELEASE_DIR}

cp iam-service/build/libs/iam-service-${VERSION}.jar ${RELEASE_DIR}
cp LICENSE ${RELEASE_DIR}
cp release/README-release.md ${RELEASE_DIR}/README.md
cp release/Dockerfile.* ${RELEASE_DIR}
cp release/docker-compose* ${RELEASE_DIR}
cp iam-service/src/main/resources/application.yml ${RELEASE_DIR}
cp iam-service/iam-service-start.sh ${RELEASE_DIR}

cd build

zip -r iam-service-release-${VERSION}.zip iam-service-${VERSION}

if [ $? = 0  ]; then
  echo -e "Release ${GREEN}OK${NOCOLOR}: build/iam-service-release-${VERSION}.zip"
else
  echo -e "ERROR: ${RED}release has failed !${NOCOLOR}"
  exit 1
fi

rm -rf ${RELEASE_DIR}
