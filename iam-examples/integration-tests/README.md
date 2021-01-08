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

### 3. User Manual test suite
```
gradle :integration-tests:clean :integration-tests:test -Dtest.profile=integration-user-manual #-Diamservice.url=http://my-iam-service.com
```
