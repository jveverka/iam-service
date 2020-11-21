## Setup Cache Layer
By default, *iam-service* uses in-memory caches for tokens and authorization codes, which is very convenient for testing purposes or simple deployments.
However, for real deployment is recommended to use __redis__ backed caches.

### Supported Cache modes
* __in-memory__ (default, single node) - __application.yml__ configuration contains:
  ```
  iam-service:
    cache-type:
      type: in-memory
  ``` 
* __redis__ - token and authorization codes are cached in [Redis](https://redis.io/).
  In this case __application.yml__ configuration contains: 
  ```
  iam-service:
    cache-type:
      type: redis
      host: localhost
      port: 6379
  ``` 

* [next step: Getting Admin access](02a-get-admin-access-token.md)