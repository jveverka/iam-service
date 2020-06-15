## Data model
![data-model](IAM-data-model.svg)

* __Organization__ - logical group of projects. 
  Self-Signed X.509 certificate is issued for organization when created.
* __Project__ - set of clients, end-users, permissions and roles.  
  Project defines audience - set of services in scope of this project.  
  X.509 certificate signed by organization is issued on project create.
* __Client__ - Identity of client with credentials and assigned roles.
* __User__ - Identity of end-user with credentials and assigned roles.  
  X.509 certificate signed by project is issued on user create.
* __Role__ - set of permissions. Project contains the set of project roles, clients and user may be assigned one or more roles from project.
* __Permission__ - access rule for particular resource. [service].[resource].[action] 

## Example
```
 Organization=[AcmeLimited]
  |
  +--Project=[ManagementSystem]
     |
     +--audience=[
     |             "sales" , "production"
     |           ]
     |
     +--permissions=[
     |                "sales.orders.create",
     |                "sales.orders.update",
     |                "sales.orders.list",
     |                "sales.reports.view",
     |                "production.project.create",
     |                "production.project.report",
     |                "production.project.close",
     |                "production.project.list"
     |                "production.reports.view",
     |              ]
     |
     +--roles
        |
        +--"Management"
        |   |
        |   +--"sales.reports.view"   
        |   +--"sales.reports.list"   
        |   +--"production.reports.view"
        |   +--"production.project.list"
        |     
        +--"Sales"   
        |   |
        |   +--"sales.reports.view"   
        |   +--"sales.reports.update"   
        |   +--"sales.reports.list"   
        |   +--"sales.reports.view"
        |   +--"production.project.list"
        |   
        +--"ProjectManager"   
        |   |
        |   +--"production.project.create"   
        |   +--"production.project.report"   
        |   +--"production.project.close"   
        |
        +--"Worker"   
            |
            +--"production.project.report"   
```