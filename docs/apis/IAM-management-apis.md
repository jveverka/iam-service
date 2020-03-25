## IAM Management REST APIs

* Create new organization using provided name and return unique __organization-id__.  
  __POST__   ``http://localhost:8080/services/management/organizations``
  ```
  { "name": "organization name" }
  ```

* Delete organization including projects and users related to this organization.  
  __DELETE__ ``http://localhost:8080/services/management/organizations/{organization-id}``

* Create new project under organization and return unique __project-id__.  
  __POST__   ``http://localhost:8080/services/management/{organization-id}/projects``
  ```
  { "name": "project name" }
  ```

* Delete project under organization.  
  __DELETE__ ``http://localhost:8080/services/management/{organization-id}/projects/{project-id}``

__WIP__
