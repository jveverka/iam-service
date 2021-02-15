#!/bin/bash
. release/common.sh

START_TIME=$(date +%s.%N)
BUILD_RESULT="${RED}FAILED${NOCOLOR}"
DOCKER_RESULT="${RED}FAILED${NOCOLOR}"
TEST_SETUP_IAMSERVICE_RESULT="${RED}FAILED${NOCOLOR}"
TEST_METHOD_SECURITY_RESULT="${RED}FAILED${NOCOLOR}"
TEST_RESOURCE_SERVER_RESULT="${RED}FAILED${NOCOLOR}"
TEST_WEBFLUX_SERVER_RESULT="${RED}FAILED${NOCOLOR}"
TEST_CLEANUP_SERVER_RESULT="${RED}FAILED${NOCOLOR}"
TEST_USER_MANUAL_RESULT="${RED}FAILED${NOCOLOR}"
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

#00. Check system dependencies.
which java
eval_result_exit $? "Java           ${GREEN}OK${NOCOLOR}" "${RED}ERROR: java not installed.${NOCOLOR}"
which gradle
eval_result_exit $? "Gradle         ${GREEN}OK${NOCOLOR}" "${RED}ERROR: gradle not installed.${NOCOLOR}"
which docker
eval_result_exit $? "Docker         ${GREEN}OK${NOCOLOR}" "${RED}ERROR: docker not installed.${NOCOLOR}"
which docker-compose
eval_result_exit $? "docker-compose ${GREEN}OK${NOCOLOR}" "${RED}ERROR: docker-compose not installed.${NOCOLOR}"

#01. Build project and run JUnit tests
gradle clean build test
if [ $? -eq  0 ]; then
   BUILD_RESULT="${GREEN}OK${NOCOLOR}"
else
   RESULT_COUNTER=$((RESULT_COUNTER+1))
fi

#02. Create and deploy docker images
docker-compose up --build -d
if [ $? -eq  0 ]; then
   DOCKER_RESULT="${GREEN}OK${NOCOLOR}"
else
   RESULT_COUNTER=$((RESULT_COUNTER+1))
fi

if [ $RESULT_COUNTER -eq 0 ]; then
   #03. Wait for all REST services to start.
   until $(curl --silent --output /dev/null -f http://127.0.0.1:8080/services/oauth2/iam-admins/iam-admins/.well-known/jwks.json ); do
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
   until [ $(curl --silent --output /dev/null -f http://127.0.0.1:8083/services/public/info -w '%{http_code}\n')  -eq  "200" ]; do
      echo "Waiting for spring-resource-server to start ..."
      sleep 1
   done

   #04. Setup iam-service
   echo -e "${YELLOW}TESTING EXAMPLE: iam-service SETUP${NOCOLOR}"
   gradle :integration-tests:clean :integration-tests:test -Dtest.profile=integration-setup
   if [ $? -eq  0 ]; then
      TEST_SETUP_IAMSERVICE_RESULT="${GREEN}OK${NOCOLOR}"
   else
      RESULT_COUNTER=$((RESULT_COUNTER+1))
   fi

   #05. Run Integration tests for 'spring-method-security' demo
   echo -e "${YELLOW}TESTING EXAMPLE: spring-method-security${NOCOLOR}"
   gradle :spring-method-security:clean :spring-method-security:test -Dtest.profile=integration
   if [ $? -eq  0 ]; then
      TEST_METHOD_SECURITY_RESULT="${GREEN}OK${NOCOLOR}"
   else
      RESULT_COUNTER=$((RESULT_COUNTER+1))
   fi

   #06. Run Integration tests for 'spring-resource-server' demo
   echo -e "${YELLOW}TESTING EXAMPLE: spring-resource-server${NOCOLOR}"
   gradle :spring-resource-server:clean :spring-resource-server:test -Dtest.profile=integration
   if [ $? -eq  0 ]; then
      TEST_RESOURCE_SERVER_RESULT="${GREEN}OK${NOCOLOR}"
   else
      RESULT_COUNTER=$((RESULT_COUNTER+1))
   fi

   #07. Run Integration tests for 'spring-webflux-secured' demo
   echo -e "${YELLOW}TESTING EXAMPLE: spring-webflux-secured${NOCOLOR}"
   gradle :spring-webflux-secured:clean :spring-webflux-secured:test -Dtest.profile=integration
   if [ $? -eq  0 ]; then
      TEST_WEBFLUX_SERVER_RESULT="${GREEN}OK${NOCOLOR}"
   else
      RESULT_COUNTER=$((RESULT_COUNTER+1))
   fi

   #08. Cleanup iam-service
   echo -e "${YELLOW}TESTING EXAMPLE: iam-service CLEANUP${NOCOLOR}"
   gradle :integration-tests:clean :integration-tests:test -Dtest.profile=integration-cleanup
   if [ $? -eq  0 ]; then
      TEST_CLEANUP_SERVER_RESULT="${GREEN}OK${NOCOLOR}"
   else
      RESULT_COUNTER=$((RESULT_COUNTER+1))
   fi

   #09. User Manual tests
   echo -e "${YELLOW}TESTING USER MANUAL: iam-service${NOCOLOR}"
   gradle :integration-tests:clean :integration-tests:test -Dtest.profile=integration-user-manual
   if [ $? -eq  0 ]; then
      TEST_USER_MANUAL_RESULT="${GREEN}OK${NOCOLOR}"
   else
      RESULT_COUNTER=$((RESULT_COUNTER+1))
   fi

fi

#09. Shutdown and Cleanup docker
docker-compose down -v --rmi all --remove-orphans
if [ $? -eq  0 ]; then
   CLEANUP_RESULT="${GREEN}OK${NOCOLOR}"
else
   RESULT_COUNTER=$((RESULT_COUNTER+1))
fi

#10. Report results
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
echo -e "IT Tests (iam setup)       : $TEST_SETUP_IAMSERVICE_RESULT"
echo -e "IT Tests (method security) : $TEST_METHOD_SECURITY_RESULT"
echo -e "IT Tests (resource server) : $TEST_RESOURCE_SERVER_RESULT"
echo -e "IT Tests (webflux server)  : $TEST_WEBFLUX_SERVER_RESULT"
echo -e "IT Tests (cleanup)         : $TEST_CLEANUP_SERVER_RESULT"
echo -e "IT Tests (user-manual)     : $TEST_USER_MANUAL_RESULT"
echo -e "docker stop and cleanup    : $CLEANUP_RESULT"
echo -e "done in $DIFF_TIME s"

exit $RESULT_COUNTER
