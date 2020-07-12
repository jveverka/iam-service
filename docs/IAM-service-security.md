# IAM Service Security Model

## Roles and Permissions
* __iam-manager__, scope: iam-admins
  * iam-service.organizations.all
  * iam-service.project.all
  * iam-service.iam-admins.all
* __iam-organization-owner__, scope: organizationId  
  * iam-service.organization.all
* __iam-project-owner__, scope: organizationId/projectId 
  * iam-service.project.all
* __iam-client__, scope: organizationId/projectId 
  * iam-service.project-client.modify
* __iam-user__, scope: organizationId/projectId
  * iam-service.project-user.modify
