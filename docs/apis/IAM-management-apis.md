## IAM Management REST APIs

* Create new organization using provided name and return unique __organization-id__.  
  __PUT__     ``http://localhost:8080/services/management/organizations``
  ```
  { "name": "organization name" }
  ```

* Delete organization including projects and users related to this organization.  
  __DELETE__  ``http://localhost:8080/services/management/organizations/{organization-id}``

__WIP__
