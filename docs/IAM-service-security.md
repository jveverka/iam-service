# IAM Service Security Model
Internal __iam-service__ security model determines level of access for 
management of organization and projects. To setup your custom 
organization and project and security roles and permissions. 

## Roles, Permissions and Scopes
For definition of Role and Permission, 
please see [this](IAM-data-model.md) chapter. 
Internal security model design, Role - Permission relation:

* __iam-admin__ role
  * *iam-service.organizations.all* 
  * *iam-service.projects.all* 
  * *iam-service.users.all* 
  * *iam-service.clients.all* 
* __iam-admin-client__ role, restricted to: iam-admin organizationId  
  * *iam-service.organization.read*     
* __iam-organization-owner__ role, restricted to: organizationId  
  * *iam-service.organization.all* 
  * *iam-service.projects.all* 
  * *iam-service.users.all* 
  * *iam-service.clients.all* 
* __iam-project-owner__ role, restricted to: organizationId/projectId 
  * *iam-service.project.all* 
  * *iam-service.users.all* 
  * *iam-service.clients.all* 
* __iam-client__ role, restricted to: organizationId/projectId/clientId
  * *iam-service.client.modify* 
* __iam-user__ role, limited to: organizationId/projectId/userId
  * *iam-service.user.modify* 


* __actions__
  * *all* - all actions on dada model such as read, create, update and delete objects
  * *read* - only read current state
  * *modify* - modify existing object properties


| permission                    | description                                         |
|-------------------------------|-----------------------------------------------------|
| iam-service.organizations.all | full organizations management, actions: read, write |
| iam-service.organization.all  | full organization management, actions: read, write  |
| iam-service.projects.all      | full project management, actions: read, write       |
| iam-service.project.all       | full project management, actions: read, write       |
| iam-service.users.all         | full user management, actions: read, write          | 
| iam-service.clients.all       | full client management, actions: read, write        |
| iam-service.client.modify     | modify own properties, set own credentials          |
| iam-service.user.modify       | modify own properties, set own credentials          |
