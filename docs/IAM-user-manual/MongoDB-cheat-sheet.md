# MongoDB cheat sheet

* Use [MongoDB CLI](https://docs.mongodb.com/manual/mongo/) 
* Connect to MongoDB and setup database and user.
  ```
  ./mongo --username iam-admin --password --host localhost:27017
  use iam-service-db
  db.createUser(
  {
    user: "iam-admin",
    pwd: passwordPrompt(),
    roles: [
       { role: "userAdmin", db: "iam-service-db" },
       { role: "dbOwner", db: "iam-service-db" },
       { role: "readWrite", db: "iam-service-db" }
    ]
  }
  )
  db.getCollectionNames()
  db.dropUser("iam-admin")
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