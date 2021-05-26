#!/bin/bash
. common.sh

HOME_DIR=`pwd`

cd ..
./run-full-test-suite.sh
eval_result_exit $? "Build ${GREEN}OK${NOCOLOR}" "${RED}ERROR: build failed !${NOCOLOR}"

VERSION=2.5.5-RELEASE
RELEASE_DIR=build/iam-service-${VERSION}

cd ${HOME_DIR}
rm -rf ${RELEASE_DIR}/iam-service-${VERSION}.zip
rm -rf ${RELEASE_DIR}
mkdir -p ${RELEASE_DIR}

cp ../iam-service/build/libs/iam-service-${VERSION}.jar ${RELEASE_DIR}
cp ../LICENSE ${RELEASE_DIR}
cp README-release.md ${RELEASE_DIR}/README.md
cp Dockerfile.* ${RELEASE_DIR}
cp docker-compose* ${RELEASE_DIR}
cp ../iam-service/src/main/resources/application.yml ${RELEASE_DIR}
cp ../iam-service/src/main/resources/application-cloud.yml ${RELEASE_DIR}
cp ../iam-service/iam-service-start.sh ${RELEASE_DIR}
cp docker-create-image.sh ${RELEASE_DIR}

cd build

zip -r iam-service-${VERSION}.zip iam-service-${VERSION}

if [ $? = 0  ]; then
  echo -e "Release ${GREEN}OK${NOCOLOR}: build/iam-service-${VERSION}.zip"
else
  echo -e "ERROR: ${RED}release has failed !${NOCOLOR}"
  exit 1
fi
