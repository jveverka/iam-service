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
    "service": "service-value" 
    "resource": "resource-value" 
    "action": "action-value" 
  }
  ```


* Get permissions on project.  
  __GET__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/permissions``  

* Delete permission from project.  
  __DELETE__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}/permissions/{permission-id}``

#### Project Role-Permission Assignment

__WIP__
