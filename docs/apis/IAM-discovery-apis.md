## Discovery REST APIs

* Get all organizations managed by this instance of IAM-service.  
  __GET__ ``http://localhost:8080/services/discovery/organizations``

* Get all project within organization.  
  __GET__ ``http://localhost:8080/services/discovery/{organization-id}/{project-id}``

* Get user info for organization and project and user with unique id provided.  
  __GET__ ``http://localhost:8080/services/discovery/{organization-id}/{project-id}/{user-id}``
