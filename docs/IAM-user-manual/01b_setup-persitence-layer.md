## Setup Persistence Layer
By default, *iam-service* uses in-memory persistence mode, which is very convenient for testing purposes.
However, for real deployment is recommended to use __file-system__ persistence layer.

### Supported Persistence modes
* __in-memory__ (default, no persistence) - __application.yml__ configuration contains:
  ```
  iam-service:
    data-model:
     persistence: in-memory
  ``` 
* __file-system__ - data is persisted into single JSON file on each model change.
  On service startup, data is loaded from same JSON file. In this case __application.yml__ configuration contains: 
  ```
  iam-service:
    data-model:
      persistence: file-system
      path: /path/to/model-storage.json
  ``` 
* __mongo-db__ - data is persisted into MongoDB.
  In this case __application.yml__ configuration contains: 
  ```
  iam-service:
    data-model:
      persistence: mongo-db
      mongo-host: localhost
      mongo-port: 27017
      mongo-database: iam-service-db
      mongo-username: iam-admin
      mongo-password: secret
  ``` 

* [next step: Getting Admin access](02a-get-admin-access-token.md)