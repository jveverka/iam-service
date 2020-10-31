#!/bin/bash

NOCOLOR='\033[0m'
RED='\033[0;31m'
GREEN='\033[0;32m'
START_TIME=$(date +%s.%N)
BUILD_RESULT="${RED}FAILED${NOCOLOR}"
DOCKER_RESULT="${RED}FAILED${NOCOLOR}"
TEST_METHOD_SECURITY_RESULT="${RED}FAILED${NOCOLOR}"
TEST_RESOURCE_SERVER_RESULT="${RED}FAILED${NOCOLOR}"
CLEANUP_RESULT="${RED}FAILED${NOCOLOR}"
CUMULATIVE_RESULT="${RED}FAILED${NOCOLOR}"
RESULT_COUNTER=0

echo ".___   _____      _____              _________                  .__              "
echo "|   | /  _  \    /     \            /   _____/ ______________  _|__| ____  ____  "
echo "|   |/  /_\  \  /  \ /  \   ______  \_____  \_/ __ \_  __ \  \/ /  |/ ___\/ __ \ "
echo "|   /    |    \/    Y    \ /_____/  /        \  ___/|  | \/\   /|  \  \__\  ___/ "
echo "|___\____|__  /\____|__  /         /_______  /\___  >__|    \_/ |__|\___  >___  >"
echo "            \/         \/                  \/     \/                    \/    \/ "

echo "Full Build & Integration Tests"
echo ""

#0. Check system dependencies.
which java
if [ $? = 0  ]; then
  echo -e "Java           ${GREEN}OK${NOCOLOR}"
else
  echo -e "${RED}ERROR: java not installed.${NOCOLOR}"
  exit 1
fi
which gradle
if [ $? = 0  ]; then
  echo -e "Gradle         ${GREEN}OK${NOCOLOR}"
else
  echo -e "${RED}ERROR: gradle not installed.${NOCOLOR}"
  exit 1
fi

which docker
if [ $? = 0  ]; then
  echo -e "Docker         ${GREEN}OK${NOCOLOR}"
else
  echo -e "${RED}ERROR: docker not installed.${NOCOLOR}"
  exit 1
fi

which docker-compose
if [ $? = 0  ]; then
  echo -e "docker-compose ${GREEN}OK${NOCOLOR}"
else
  echo -e "${RED}ERROR: docker-compose not installed.${NOCOLOR}"
  exit 1
fi

#1. Build project and run JUnit tests
gradle clean build test
if [ $? -eq  0 ]; then
   BUILD_RESULT="${GREEN}OK${NOCOLOR}"
else
   RESULT_COUNTER=$((RESULT_COUNTER+1))
fi

#2. Create and deploy docker images
docker-compose up --build -d
if [ $? -eq  0 ]; then
   DOCKER_RESULT="${GREEN}OK${NOCOLOR}"
else
   RESULT_COUNTER=$((RESULT_COUNTER+1))
fi

if [ $RESULT_COUNTER -eq 0 ]; then
   #3. Wait for all REST services to start.
   until $(curl --silent --output /dev/null -f http://127.0.0.1:8080/services/authentication/iam-admins/iam-admins/.well-known/jwks.json ); do
      echo "Waiting for iam-service to start ..."
      sleep 1
   done
   until [ $(curl --silent --output /dev/null -f http://127.0.0.1:8082/services/public/info -w '%{http_code}\n')  -eq  "200" ]; do
      echo "Waiting for spring-method-security to start ..."
      sleep 1
   done
   until [ $(curl --silent --output /dev/null -f http://127.0.0.1:8081/services/public/info -w '%{http_code}\n')  -eq  "401" ]; do
      echo "Waiting for spring-resource-server to start ..."
      sleep 1
   done

   #4. Run Integration tests for 'spring-method-security' demo
   gradle :spring-method-security:clean :spring-method-security:test -Dtest.profile=integration
   if [ $? -eq  0 ]; then
      TEST_METHOD_SECURITY_RESULT="${GREEN}OK${NOCOLOR}"
   else
      RESULT_COUNTER=$((RESULT_COUNTER+1))
   fi

   #5. Run Integration tests for 'spring-resource-server' demo
   gradle :spring-resource-server:clean :spring-resource-server:test -Dtest.profile=integration
   if [ $? -eq  0 ]; then
      TEST_RESOURCE_SERVER_RESULT="${GREEN}OK${NOCOLOR}"
   else
      RESULT_COUNTER=$((RESULT_COUNTER+1))
   fi

fi

#6. Shutdown and Cleanup docker
docker-compose down -v --rmi all --remove-orphans
if [ $? -eq  0 ]; then
   CLEANUP_RESULT="${GREEN}OK${NOCOLOR}"
else
   RESULT_COUNTER=$((RESULT_COUNTER+1))
fi

#7. Report results
END_TIME=$(date +%s.%N)
DIFF_TIME=$(echo "$END_TIME - $START_TIME" | bc)
if [ $RESULT_COUNTER -eq  0 ]; then
   CUMULATIVE_RESULT="${GREEN}OK${NOCOLOR}"
fi

echo -e ""
echo -e "Full Test Suite Results    : $CUMULATIVE_RESULT"
echo -e "============================"
echo -e "gradle build and test      : $BUILD_RESULT"
echo -e "docker compose             : $DOCKER_RESULT"
echo -e "IT Tests (method security) : $TEST_METHOD_SECURITY_RESULT"
echo -e "IT Tests (resource server) : $TEST_RESOURCE_SERVER_RESULT"
echo -e "docket stop and cleanup    : $CLEANUP_RESULT"
echo -e "done in $DIFF_TIME s"

exit $RESULT_COUNTER
