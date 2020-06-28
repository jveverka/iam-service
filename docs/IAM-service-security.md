# IAM Service Security Model

## Roles and Permissions
* iam-manager
  * iam-service.organizations.all
  * iam-service.project.all
  * iam-service.iam-admins.all
* iam-project-owner
  * iam-service.project.all
* iam-client
  * iam-service.project-client.modify
* iam-user
  * iam-service.project-user.modify
* anonymous - unauthorized clients
  * iam-service.organizations.read
  * iam-service.projects.read
