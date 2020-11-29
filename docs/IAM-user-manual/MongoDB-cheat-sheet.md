# MongoDB cheat sheet

* Use [MongoDB CLI](https://docs.mongodb.com/manual/mongo/) 
* Connect to MongoDB
  ```
  ./mongo --username iam-admin --password --host localhost:27017
  use  iam-service-db
  db.getCollectionNames()
  ```
* List collections [ "clients", "modelinfo", "organizations", "projects", "roles", "users" ]  
  ```
  db.modelinfo.find()
  db.organizations.find()
  db.projects.find()
  db.clients.find()
  db.users.find()
  db.roles.find()
  ```