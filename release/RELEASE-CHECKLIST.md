# iam-service release checklist

* Make sure all changes are committed and pushed.
* Make sure the documentation is up-to date.
* Make sure all tests are passing.
* ``NEW_RELEASE_VERSION=2.4.3-RELEASE``
* Upgrade component version in script below.
  ```
  ./version-upgrade.sh
  ```
* Commit changes related to version upgrade and push to origin.
  ```
  git add .
  git commit -m  "creating release <NEW_RELEASE_VERSION>"
  git push origin <RELEASE_BRANCH>
  ```
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
* Publish artefacts to 
  [oss.sonatype.org SNAPSHOT](https://oss.sonatype.org/content/repositories/snapshots) public repository or
  [oss.sonatype.org stage](https://oss.sonatype.org/service/local/staging/deploy/maven2).
  ```
  gradle publishToMavenLocal
  gradle publish
  ```
* Create docker image locally.
  ```
  cd build/iam-service-<NEW_RELEASE_VERSION>
  ./docker-create-image.sh 
  ```
* Test created docker image.
  ```
  docker logs --follow iam-service-2.4.3-RELEASE 
  docker exec -it iam-service-2.4.3-RELEASE /bin/sh
  ```
* Publish docker image to dockerhub.
  ```
  docker push jurajveverka/iam-service:${VERSION}-${ARCH}
  ```
  