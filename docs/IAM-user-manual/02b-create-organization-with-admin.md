## Create new Organizations and Projects
*iam-service* uses [hierarchical](../IAM-data-model.md) Organization/Project structure. Each project is
isolated security realm with own group of users, clients, roles and permissions.
[See also: Terms and Vocabulary](Terms-and-Vocabulary.md)

* Create new Organization and one Project under same organization with admin user with proper access permissions.  
  ```
  curl --location --request POST 'http://localhost:8080/services/admin/organization/setup' \
  --header 'Authorization: Bearer <ACCESS_TOKEN>' \
  --header 'Content-Type: application/json' \
  --data-raw '{ 
     "organizationId": "test-org-001", 
     "organizationName": "Test Organization 001", 
     "adminProjectId": "project-001",
     "adminProjectName": "Project 001",
     "adminClientId": "cl-001",
     "adminClientSecret": "cl-scrt",
     "adminUserId": "admin",
     "adminUserPassword": "some*top+sercret",
     "adminEmail": "admin@project-001.com",
     "projectAudience": [],
     "adminUserProperties": { 
       "properties": {}
     }
  }'
  ```
  This will single request will create:   
  * Organization with ID ``test-org-001``
  * Project with ID ``project-001`` under organization ``test-org-001``
  * Admin user ``admin`` for project ``project-001`` with permissions to manage that project. 
  
* [next step: Manage your new Organization and Projects](02c-manage-organization-and-projects.md)
  