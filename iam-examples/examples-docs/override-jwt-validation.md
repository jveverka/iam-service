# Overriding JWT token validation

This demo shows, how to override default implementation of JWT token validator in
__iam-service__ and replace it with custom one.

1. [TokenValidator](../../iam-common/iam-model/src/main/java/one/microproject/iamservice/core/TokenValidator.java) -
   __iam-service__ uses default implementation of TokenValidator.
2. [TokenValidatorImpl](../../iam-common/iam-client/src/main/java/one/microproject/iamservice/client/impl/TokenValidatorImpl.java) -
   default implementation of TokenValidator used by __iam-service__.
3. Create your own implementation of TokenValidator service.
4. Plug-in your own implementation of TokenValidator into __iam-service__. 
   It is recommended, that same implementation of TokenValidator is used 
   also by external clients like [iam-client](../../iam-common/iam-client).   
