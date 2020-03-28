# IAM-core

Important core components and interfaces:
* __IAMCoreBuilder__ - initialization process of IAM-core services.
* __Model__ - in-memory data model containing full scope of IAM.
  * __Model__ - top-level object.
  * __Organization__ - grouping projects of one organization
  * __Project__  
  * __User__
  * __Client__
  * __Role__
  * __Permission__  
  * __Credentials__
* __Services__ - APIs to manipulate data model (queries and actions).
  * __ClientService__
  * __ResourceServerService__ 
  * __ClientManagementService__
  * __OrganizationManagerService__
  * __ProjectManagerService__
  * __UserManagerService__
* __Caches__ - token caches for intermediate token storage.
  * __AuthorizationCodeCache__
  * __TokenCache__  
  * __CacheCleanupScheduler__