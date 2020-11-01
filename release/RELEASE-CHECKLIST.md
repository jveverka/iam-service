# iam-service release checklist

* Make sure all changes are committed and pushed.
* Make sure the documentation is up-to date.
* Make sure all tests are passing.
* ``NEW_RELEASE_VERSION=1.2.0-SNAPSHOT``
* Upgrade component version in script below.
  ```
  ./version-upgrade.sh
  ```
* Test all iam-service components locally.
  ```
  cd release
  ./create-release.sh
  cd ..
  ```
* Commit changes related to version upgrade.
* Create release tag and push the tag to origin.  
  ```
  git tag -l 
  git tag RELEASE_<NEW_RELEASE_VERSION>
  git push origin --tags
  ```
* Create new release on [github](https://github.com/jveverka/iam-service/releases)  
  * Make sure Release notes are up-to-date.
  * Upload released binary 
    ```
    build/iam-service-release-<NEW_RELEASE_VERSION>.zip
    ```
* Publish artefacts to as [described here](https://central.sonatype.org/pages/gradle.html).
  ```
  gradle uploadArchives
  ```