## Manage your new Organization / Projects
At this point new Organization / Projects has been already created, now it is  
necessary to create Application users, user identities which will access other microservices.
It is also necessary to assign new roles and permissions to new users. 
All actions described below must be performed under project admin user identity.
##### See also: 
* [Terms and Vocabulary](Terms-and-Vocabulary.md)
* [Security Model](IAM-Service-Security-Model.md) 

##### Get Access Token to Manage your Organization/Project
* Get you Organization/Project Admin Access tokens  
  ```
  #template:
  #curl --location --request POST 'http://localhost:8080/services/oauth2/{organization-id}/{project-id}/token?grant_type=password&username={admin-user}&password={****}&scope=&client_id={admin-client}&client_secret={*****}'
  curl --location --request POST 'http://localhost:8080/services/oauth2/test-org-001/project-001/token?grant_type=password&username=admin&password=some-top-sercret&scope=&client_id=cl-001&client_secret=cl-scrt' \
  --header 'Content-Type: application/x-www-form-urlencoded'
  ```

### Manage Clients
* Create new Client 
  ```
  #template:
  #curl --location --request POST 'http://localhost:8080/services/management/{organization-id}/{project-id}/clients' \
  curl --location --request POST 'http://localhost:8080/services/management/test-org-001/project-001/clients' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "id": "client-002",
      "name": "Second Client",
      "defaultAccessTokenDuration": 3600000,
      "defaultRefreshTokenDuration": 3600000,
      "secret": "s3cr3t",
      "properties":  {
          "redirectURL": "",
          "authorizationCodeGrantEnabled": true,
          "passwordCredentialsEnabled": true,
          "clientCredentialsEnabled":  true,
          "properties": {
          }
      }
  }'
  ```
* Delete Client
  ```
  #template:
  #curl --location --request DELETE 'http://localhost:8080/services/management/{organization-id}/{project-id}/clients/{client-id}' \
  curl --location --request DELETE 'http://localhost:8080/services/management/test-org-001/project-001/clients/client-002' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>'
  ```
* Get Client on Project
  ```
  #template:
  #curl --location --request GET 'http://localhost:8080/services/discovery/{organization-id}/{project-id}/clients/{client-id}' \
  curl --location --request GET 'http://localhost:8080/services/discovery/test-org-001/project-001/clients/client-002' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>'
  ```

### Manage Users
* Create new User
  ```
  #template:
  #curl --location --request POST 'http://localhost:8080/services/management/{organization-id}/{project-id}/users' \
  curl --location --request POST 'http://localhost:8080/services/management/test-org-001/project-001/users' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "id": "user-001",
      "name": "User Name",
      "defaultAccessTokenDuration": 3600000,
      "defaultRefreshTokenDuration": 3600000,
      "email": "user@server.com",
      "password": "secret",
      "userProperties": {
          "properties": {}
      }
  }'
  ```
* Change user's credentials
  ```
  #template:
  #curl --location --request PUT 'http://localhost:8080/services/management/{organization-id}/{project-id}/users/{user-id}/change-password' \
  curl --location --request PUT 'http://localhost:8080/services/management/test-org-001/project-001/users/user-001/change-password' \
  --header 'Authorization: Bearer <USER_ACCESS_TOKEN>' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "newPassword": "new-secret-password"
  }' 
  ```
* Delete User
  ```
  #template:
  #curl --location --request DELETE 'http://localhost:8080/services/management/{organization-id}/{project-id}/users/{user-id}' \
  curl --location --request DELETE 'http://localhost:8080/services/management/test-org-001/project-001/users/user-001' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>'    
  ```
* Get User
  ```
  #template:
  #curl --location --request GET 'http://localhost:8080/services/discovery/{organization-id}/{project-id}/users/{user-id}' \
  curl --location --request GET 'http://localhost:8080/services/discovery/test-org-001/project-001/users/user-001' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>'
  ```

### Manage Roles
* Create new Role with Permissions
  ```
  #template:
  #curl --location --request POST 'http://localhost:8080/services/management/{organization-id}/{project-id}/roles' \
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
  #template:
  #curl --location --request DELETE 'http://localhost:8080/services/management/{organization-id}/{project-id}/roles/{role-id}' \
  curl --location --request DELETE 'http://localhost:8080/services/management/test-org-001/project-001/roles/role-001' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>' \
  ```
* Add Role to User
  ```
  #template:
  #curl --location --request PUT 'http://localhost:8080/services/management/{organization-id}/{project-id}/users/{user-id}/roles/{role-id}
  curl --location --request PUT 'http://localhost:8080/services/management/test-org-001/project-001/users/user-001/roles/role-001
  --header 'Authorization: Bearer <ACCESS_TOKEN>'
  ```
* Remove Role from User
  ```
  #template:
  #curl --location --request DELETE 'http://localhost:8080/services/management/{organization-id}/{project-id}/users/{user-id}/roles/{role-id}
  curl --location --request DELETE 'http://localhost:8080/services/management/test-org-001/project-001/users/user-001/roles/role-001
  --header 'Authorization: Bearer <ACCESS_TOKEN>'
  ```
* Get Permissions on the Project
  ```
  #template:
  #curl --location --request GET 'http://localhost:8080/services/management/{organization-id}/{project-id}/permissions' \
  curl --location --request GET 'http://localhost:8080/services/management/test-org-001/project-001/permissions' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>'
  ```
* Get Roles on the Project
  ```
  #template:
  #curl --location --request GET 'http://localhost:8080/services/management/{organization-id}/{project-id}/roles' \
  curl --location --request GET 'http://localhost:8080/services/management/test-org-001/project-001/roles' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>'
  ```

* [next step: Get Access_Tokens for new users](02d-getting-access-tokens-for-new-users.md)
