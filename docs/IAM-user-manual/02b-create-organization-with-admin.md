## Create Organization / Project with Admin user
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
    "projectAudience": []
  }
  ```