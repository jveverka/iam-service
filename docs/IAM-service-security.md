# IAM Service Security Model
Internal __iam-service__ security model determines level of access for 
management of organization and projects. To setup your custom 
organization and project and security roles and permissions, 
[follow](IAM-service-project-security.md) this guide. 

## Roles, Permissions and Scopes
For definition of Role and Permission, 
please see [this](IAM-data-model.md) chapter. 
Internal security model design, Role - Permission relation:

* __iam-admin__ role
  * *iam-service.organizations.all* - full organization management, actions: read, write.
  * *iam-service.projects.all* - full project management, actions: read, write.
  * *iam-service.users.all* - full user management, actions: read, write.
  * *iam-service.clients.all* - full client management, actions: read, write.
* __iam-organization-owner__ role, restricted to: organizationId  
  * *iam-service.organization.all* - full organization management, actions: read, write.
  * *iam-service.projects.all* - full project management, actions: read, write.
  * *iam-service.users.all* - full user management, actions: read, write.
  * *iam-service.clients.all* - full client management, actions: read, write.
* __iam-project-owner__ role, restricted to: organizationId/projectId 
  * *iam-service.project.all* - full project management, actions: read, write.
  * *iam-service.users.all* - full user management, actions: read, write.
  * *iam-service.clients.all* - full client management, actions: read, write.
* __iam-client__ role, restricted to: organizationId/projectId/clientId
  * *iam-service.client.modify* - modify own properties, set own credentials.
* __iam-user__ role, limited to: organizationId/projectId/userId
  * *iam-service.user.modify* - modify own properties, set own credentials.
