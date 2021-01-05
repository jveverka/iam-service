## IAM Admin REST APIs

### Admin Services
* __POST__ ``http://localhost:8080/services/admin/organization``
* __POST__ ``http://localhost:8080/services/admin/organization/setup``
* __DELETE__ ``http://localhost:8080/services/admin/organization/{organization-id}``
* __DELETE__ ``http://localhost:8080/services/admin/organization/{organization-id}/{project-id}``

### Project Management
* __POST__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/roles``
* __GET__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/roles``
* __GET__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/permissions``
* __DELETE__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/permissions/{permission-id}``
* __DELETE__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/roles/{role-id}``  
* __PUT__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/audience``

### Project User Management
* __POST__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/users``
* __DELETE__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/users/{user-id}``
* __PUT__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/users/{user-id}/change-password``
* __PUT__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/users/{user-id}/roles/{role-id}``
* __DELETE__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/users/{user-id}/roles/{role-id}``

### Project Client Management
* __POST__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/clients``
* __PUT__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/clients/{client-id}/roles/{role-id}``
* __DELETE__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/clients/{client-id}/roles/{role-id}``
* __DELETE__ ``http://localhost:8080/services/management/{organization-id}/{project-id}/clients/{client-id}``
