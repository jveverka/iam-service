## Manage your new Organization / Projects
At this point new Organization / Projects has been already created, now it is  
necessary to create Application users, user identities which will access other microservices.
It is also necessary to assign new roles and permissions to new users. 
All actions described below must be performed under project admin user identity.
##### See also: 
* [Terms and Vocabulary](Terms-and-Vocabulary.md)
* [Default Configuration](Default-Access-Configuration.md) 

##### Get Access Token to Manage your Organization/Project
* Get you Organization/Project Admin Access tokens  
  ```
  curl --location --request POST 'http://localhost:8080/services/authentication/<organization-id>/<project-id>/token?grant_type=password&username=<admin-user>&password=<****>&scope=&client_id=<admin-client>&client_secret=<*****>'
  
  # example:
  curl --location --request POST 'http://localhost:8080/services/authentication/test-org-001/project-001/token?grant_type=password&username=admin&password=some-top-sercret&scope=&client_id=cl-001&client_secret=cl-scrt'
  ```

### Manage Roles
* Create new Role with Permissions
  ```
  curl --location --request POST 'http://localhost:8080/services/management/test-org-001/project-001/roles' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "id": "role-001",
    "name": "Role 001",
    "permissions": [
        {
            "service": "service1",
            "resource": "resource1",
            "action": "read"
        },
                {
            "service": "service1",
            "resource": "resource2",
            "action": "write"
        }
    ]
  }'  
  ```
* Delete Role
  ```
  curl --location --request DELETE 'http://localhost:8080/services/management/test-org-001/project-001/roles/role-001' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>' \
  ```
* Get Roles on project
  ```
  curl --location --request GET 'http://localhost:8080/services/management/<organization-id>/<project-id>/roles' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>'
  
  # example:
  curl --location --request GET 'http://localhost:8080/services/management/test-org-001/project-001/roles' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>'
  ```

### Manage Permissions
* Create new Permission
* Delete Permission
* Add Permission to Role
* Remove Permission from Role

### Manage Clients
* Create new Client 
* Delete Client

### Manage Users
* Create new User
* Delete User

* [next step: Get Access_Tokens for new users](02d-getting-access-tokens-for-new-users.md)
