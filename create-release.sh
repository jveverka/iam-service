#!/bin/bash

./run-full-test-suite.sh
if [ $? = 0  ]; then
  echo "Build OK"
else
  echo "ERROR: build failed !"
  exit 1
fi

HOME_DIR=`pwd`
VERSION=1.0.0-SNAPSHOT
RELEASE_DIR=build/iam-service-${VERSION}

cd ${HOME_DIR}
rm -rf ${RELEASE_DIR}/iam-service-release-${VERSION}.zip
rm -rf ${RELEASE_DIR}
mkdir -p ${RELEASE_DIR}

cp iam-service/build/libs/iam-service-${VERSION}.jar ${RELEASE_DIR}
cp LICENSE ${RELEASE_DIR}
cp release/README-release.md ${RELEASE_DIR}/README.md
cp release/Dockerfile.* ${RELEASE_DIR}
cp iam-service/src/main/resources/application.yml ${RELEASE_DIR}

cd build

zip -r iam-service-release-${VERSION}.zip iam-service-${VERSION}

if [ $? = 0  ]; then
  echo "Release OK"
else
  echo "ERROR: release has failed !"
  exit 1
fi

rm -rf ${RELEASE_DIR}
