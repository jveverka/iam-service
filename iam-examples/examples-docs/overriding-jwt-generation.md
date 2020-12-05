# Overriding JWT token generation

This demo shows, how to override default implementation of JWT token generator in 
__iam-service__ and replace it with custom one.

1. [TokenGenerator](../../iam-core/src/main/java/one/microproject/iamservice/core/services/TokenGenerator.java) - 
   __iam-service__ uses default implementation of TokenGenerator. 
2. [TokenGeneratorImpl](../../iam-core/src/main/java/one/microproject/iamservice/core/services/impl/TokenGeneratorImpl.java) -
   default implementation of TokenGenerator used by __iam-service__.
3. Create your own implementation of TokenGenerator service.  
4. Plug-in your own implementation of TokenGenerator into __iam-service__.
