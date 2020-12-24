# iam-service release checklist

* Make sure all changes are committed and pushed.
* Make sure the documentation is up-to date.
* Make sure all tests are passing.
* ``NEW_RELEASE_VERSION=2.4.0-SNAPSHOT``
* Upgrade component version in script below.
  ```
  ./version-upgrade.sh
  ```
* Commit changes related to version upgrade.
* Create release tag and push the tag to origin.
  ```
  git tag -l 
  git tag v<NEW_RELEASE_VERSION>
  git push origin --tags
  ```
* Test all iam-service components locally.
  ```
  ./create-release.sh
  ```
* Create new release on [github](https://github.com/jveverka/iam-service/releases)  
  * Make sure Release notes are up-to-date.
  * Upload released binary 
    ```
    build/iam-service-<NEW_RELEASE_VERSION>.zip
    ```
* Publish artefacts to as [described here](https://central.sonatype.org/pages/gradle.html).
  ```
  gradle uploadArchives
  ```
* Create docker image locally.
  ```
  cd build/iam-service-<NEW_RELEASE_VERSION>
  ./docker-create-image.sh 
  ```
* Test created docker image.
  ```
  docker logs --follow iam-service-2.4.0-SNAPSHOT 
  docker exec -it iam-service-2.4.0-SNAPSHOT /bin/sh
  ```
* Publish docker image to dockerhub.
  ```
  docker push jurajveverka/iam-service:${VERSION}-${ARCH}
  ```
  