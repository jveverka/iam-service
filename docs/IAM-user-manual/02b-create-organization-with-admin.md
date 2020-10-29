## Create new Organizations and Projects
TODO

* __POST__ ``http://localhost:8080/services/admin/organization/setup``
  ```
  {
    "organizationId": "test-org-001",
    "organizationName": "",
    "adminProjectId": "project-001",
    "adminProjectName": "",
    "adminClientId": "cl-001",
    "adminClientSecret": "cl-scrt",
    "adminUserId": "admin1",
    "adminUserPassword": "top",
    "adminEmail": "admin@project-001.com",
    "projectAudience": []
  }
  ```
  
* [next step: Manage your new Organization and Projects](02c-manage-organization-and-projects.md)
  