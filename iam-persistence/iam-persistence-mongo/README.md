# Mongo DB IAM Persistence

### Install MongoDB on locally
Download MongoDB binary from [here](https://www.mongodb.com/try/download/community). 
Java documentation is [here](https://mongodb.github.io/mongo-java-driver/).
- unpack mongo tar package  
  ```tar xzvf mongodb-linux-x86_64-ubuntu2004-4.4.1.tgz```
- make database directory  
  ```mkdir -p mongodb-linux-x86_64-ubuntu2004-4.4.1/data/db```  
- run mongodb server  
  ```
  cd mongodb-linux-x86_64-ubuntu2004-4.4.1/bin
  ./mongod --dbpath ../data/db
  ```
- create database and user  
  ```
  cd mongodb-linux-x86_64-ubuntu2004-4.4.1/bin
  ./mongo
  use iam-service-db
  db.createUser({user: "iam-service", pwd: "secret", roles: [ "readWrite", "dbAdmin" ]}) 
  ```
- setup is complete. next time start mongodb with command  
  ```./mongod --dbpath ../data/db```  