# IAM-Service Security Model
By default, in case internal data model of *iam-service* is not populated, default data model is created automatically on *iam-service* startup.
Default data model always contains one organization __iam-admins__ and one project __iam-admins__ with single admin user 
as described below. This single identity is the entry point to start using *iam-service*. Using this global admin identity, you can create your own organization(s) and 
project(s). Each project should have own local 'admin' user capable of managing just only own project.

## 1. Global ADMIN - SuperUser(s)
Global admin users are very important, because such users are capable of:
* Create organizations, projects and project admin users.
* Delete organizations, projects recursively.

This is how default data model with single global admin user looks like:
* __iam-admins__ - organization containing admin project
  * __iam-admins__ - project containing admin user(s)
    * __admin-client__ - client for admin user
    * __admin__ - user with global admin permissions

![iam-admins](schemas/default_iam-admins_organization_model.svg)

At least one admin user must exist in order to use IAM Admin
functions listed above. Each *global admin* user must have following minimal set of permissions:
* ``iam-admin-service.organization.all``
* ``iam-admin-service.project.all``
* ``iam-admin-service.users.all``
* ``iam-admin-service.clients.all``

## 2. Organizations / Project Admin(s)
Each organization and project requires it's own admin user. Organization and project 
structure below is created like described [here](02b-create-organization-with-admin.md) using access_token obtained from *iam-admin* as described [here](02a-get-admin-access-token.md).  

![project-model](schemas/default_organization-project_model.svg)

At least one admin user must exist per organization to use Organization/Project Admin functions.
Each Organization/Project Admin user must have following minimal set of permissions: 
* ``<organization-id>-<project-id>.organization.all``
* ``<organization-id>-<project-id>.iam-admin-service.project.all``
* ``<organization-id>-<project-id>.iam-admin-service.users.all``
* ``<organization-id>-<project-id>.iam-admin-service.clients.all``

## 3. Common Users and Clients
Each project hosts several users and clients. Those are common users and clients not suitable for organization and project admin tasks.
Such users and client are intended to be used with resource servers. Set of roles and
permissions assigned to those users is completely on project and required application.

#### See also:
* How to set [secrets in default data model](01a-standalone-server-config.md).
* [Setup persistence Layer](01b_setup-persitence-layer.md) and [Caching](01c_setup-cache-layer.md).
