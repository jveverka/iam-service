## IAM Management REST APIs

### Organization management

* Create new organization using provided name and return unique __organization-id__.  
  __POST__   ``http://localhost:8080/services/management/organizations``
  ```
  { "name": "organization name" }
  ```

* Delete organization including projects and users related to this organization.  
  __DELETE__ ``http://localhost:8080/services/management/organizations/{organization-id}``

### Project Management

* Create new project under organization and return unique __project-id__.  
  __POST__   ``http://localhost:8080/services/management/{organization-id}/projects``
  ```
  { "name": "project name" }
  ```

* Delete project under organization.  
  __DELETE__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}``

#### Project Role Management

* Create role on project and return unique __role-id__.  
  __POST__   ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/permissions``
  ```
  { "name": "role name" }
  ```

* Get roles on project.  
  __GET__    ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/permissions``

* Delete role from project.  
  __DELETE__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/permissions/{role-id}``

#### Project Permission Management

* Create permission om project and return unique __permission-id__.  
  __POST__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/permissions``
  ```
  { 
    "service": "service-value", 
    "resource": "resource-value", 
    "action": "action-value" 
  }
  ```


* Get permissions on project.  
  __GET__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/permissions``  

* Delete permission from project.  
  __DELETE__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/permissions/{permission-id}``

#### Project Role-Permission Assignment

* Add permission to role on project.   
  __PUT__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/roles-permissions/{role-id}/{permission-id}``

* Remove permission from role on project.  
  __DELETE__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/roles-permissions/{role-id}/{permission-id}``


#### Project Clients Management 

* Create client and return unique __client-id__.   
  __POST__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/clients``
  ```
  { 
    "name": "client-name", 
    "defaultAccessTokenDuration": 3600, 
    "defaultRefreshTokenDuration": 14400 
  }  
  ```

* Get client by it's unique ID.  
  __GET__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/clients/{client-id}``

* Get  all clients for this project.  
  __GET__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/clients``

* Delete client by ID.  
  __GET__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/clients/{client-id}``

#### Client Role Management

* Assign role to client.  
  __PUT__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/clients/{client-id}/roles/{role-id}``

* Remove role from client.  
  __DELETE__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/clients/{client-id}/roles/{role-id}``


### User Management
__WIP__

#### User Role Management
__WIP__

