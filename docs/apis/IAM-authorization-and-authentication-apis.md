## Authorization and Authentication REST APIs

* Issue *access_token*, *refresh_token* and *id_token*.
  Supported grant types: grant_type = authorization_code | refresh_token | password | client_credentials  
  __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/token``

* Revoke issued JWTs.  
  __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/revoke``

* Redirects to *redirect_uri* in Authorization Code Grant flow and issues access_code token.  
  __GET__  ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/login``

* Endpoint to invoke login for Authorization Code Grant flow.    
  __GET__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/authorize``

* Endpoint to provide consent for Authorization Code Grant flow.   
  __GET__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/consent``

* Redirect URI endpoint to test Authorization Code Grant flow for organization/project.   
  __GET__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/redirect``
  