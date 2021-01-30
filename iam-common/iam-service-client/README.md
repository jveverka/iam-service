# IAM Service Client
Java client library providing APIs for all iam-service management functions.
 
### Easy to use
1. Add project dependency.
   * maven dependency
     ```
     <dependency>
       <groupId>one.microproject.iamservice</groupId>
       <artifactId>iam-service-client</artifactId>
       <version>2.5.1-RELEASE</version>
     <dependency/>
     ```
   * gradle dependency
     ```
     implementation 'one.microproject.iamservice:iam-service-client:2.5.1-RELEASE'
     ```
2. Create client instance programmatically.
   ```
   IAMServiceClientBuilder iamServiceClient = IAMServiceClientBuilder.builder()
            .withBaseUrl(new URL("http://localhost:8080"))
            .withConnectionTimeout(5L, TimeOut.SECONDS)
            .build();
   ```
3. Manage *iam-service* remotely, using interfaces.
   * [IAMServiceManagerClient](src/main/java/one/microproject/iamservice/serviceclient/IAMServiceManagerClient.java)
     * [IAMAuthorizerClient](src/main/java/one/microproject/iamservice/serviceclient/IAMAuthorizerClient.java)
     * [IAMServiceProjectManagerClient](src/main/java/one/microproject/iamservice/serviceclient/IAMServiceProjectManagerClient.java)
     * [IAMServiceStatusClient](src/main/java/one/microproject/iamservice/serviceclient/IAMServiceStatusClient.java)
     * [IAMServiceUserManagerClient](src/main/java/one/microproject/iamservice/serviceclient/IAMServiceUserManagerClient.java)
      