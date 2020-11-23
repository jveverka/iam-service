# IAM-Service Terms and Vocabulary
See also: 
* [Default Configuration](Default-Access-Configuration.md) 
* [Data Model](../IAM-data-model.md)

## Organizations
Organization is just a logical group of projects.

### Projects
Project is security realm containing users and clients.
* __scope__ - defines the scope of this project.

#### Project's Admin users
Each project must have dedicated __project admin user__. This user is automatically created when 
[new Project is created](02b-create-organization-with-admin.md). __project admin user__ is
able to fully mage project. 

#### Clients
Unique identity of client device with credentials (client-id and client-secret)
* Client may have one or more roles assigned.
* Client's permissions is set of permission defined by union of all role permission sets for this Client.
* Client properties:
  * Redirect URL
  * authorizationCodeGrantEnabled
  * passwordCredentialsEnabled
  * clientCredentialsEnabled

#### Users
Unique identity of end-user (person) with email, username and credentials. 
* User may have one or more roles assigned.
* User's permissions is set of permission defined by union of all role permission sets for this User.

#### Role
Role is just a logical group (a set) of permissions. One Role contains
onr or more permissions.

#### Permission
Permission is a definition of service + resource + action. One permission 
defines target service and resource and action which may be performed upon 
this service and resource. Examples:
* __mail-service.inbox.can-read__
* __mail-service.inbox.can-send__
* __mail-service.inbox.can-delete__
* __mail-service.contacts.can-read__
* __mail-service.contacts.can-create__
* __mail-service.contacts.can-delete__
* __mail-service.contacts.*__

Implementation and security model in target resource-server (microservice) always depends
on target implementation and security approach.
