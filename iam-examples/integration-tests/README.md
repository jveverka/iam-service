# Integration Tests
Integration tests are designed to run against deployed __iam-service__.
Test suites below are used during CI/CD test verification cycle, 
as well as to verify iam-service deployment.

### 1. Setup test suite
```
gradle :integration-tests:clean :integration-tests:test -Dtest.profile=integration-setup #-Diamservice.url=http://my-iam-service.com
```

### 2. Cleanup test suite
```
gradle :integration-tests:clean :integration-tests:test -Dtest.profile=integration-cleanup #-Diamservice.url=http://my-iam-service.com
```

### 3. IAM-Service certification test suite
This test suite is designed to test deployed *iam-service* instance.
Input parameters are very important for this test suite. 
Default values are used in example below.
```
gradle :integration-tests:clean :integration-tests:test -Dtest.profile=integration-user-manual \
  -Diamservice.url=http://localhost:8080 \
  -Dadmin.pwd=secret \
  -Dclient.secret=top-secret
```
