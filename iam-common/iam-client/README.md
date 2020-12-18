## IAM-Client for Java
This client library makes JWT verification easier for clients and micro-services (resource-servers). 

### Easy to use
1. Add project dependency.
   * maven dependency
     ```
     <dependency>
       <groupId>one.microproject.iamservice</groupId>
       <artifactId>iam-client</artifactId>
       <version>2.3.0-SNAPSHOT</version>
     <dependency/>
     ```
   * gradle dependency
     ```
     implementation 'one.microproject.iamservice:iam-client:2.3.0-SNAPSHOT'
     ```
2. Create client instance programmatically.
   ```
   IAMClient iamClient = IAMClientBuilder.builder()
            .setOrganizationId("org-01")
            .setProjectId("project-01")
            .withHttpProxy(new URL("http://localhost:8080/services/oauth2"), 10L, TimeUnit.SECONDS)
            .build();
   ```
3. Verify and validate incoming JWT(s).
   ```
   while(iamClient.waitForInit(10L, TimeUnit.SECONDS)) {
   }
   iamClient.updateKeyCache();
   HttpServletRequest httpServletRequest = ...;
   JWToken jwt = JWTUtils.extractJwtToken(httpServletRequest.getHeader("Authorization"));
   iamClient.validate(jwt);
   ```
4. Check [AIMClient API](src/main/java/one/microproject/iamservice/client/IAMClient.java) for other validation options.   
