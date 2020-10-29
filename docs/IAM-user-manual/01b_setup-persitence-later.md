## Setup Persistence Layer

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
* [next step: Getting Admin access](02d-getting-access-tokens-for-new-users.md)