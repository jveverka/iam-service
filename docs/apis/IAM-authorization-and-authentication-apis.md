## Authorization and Authentication REST APIs

*  __Base url__ in this case is ``http://localhost:8080/services/authentication/{organization-id}/{project-id}``    
* Provider configuration as required by [RFC8414](https://tools.ietf.org/html/rfc8414#section-3), 
  [OpenID](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) or 
  [Provider Meta-Data](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata)  
  __GET__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/.well-known/openid-configuration``

* Provider configuration __jwks_uri__ as specified by [RFC7517](https://tools.ietf.org/html/rfc7517) JSON Web Key (JWK).  
  __GET__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/.well-known/jwks.json``

* Issue *access_token*, *refresh_token* and *id_token*.
  Supported grant types: grant_type = authorization_code | refresh_token | password | client_credentials  
  __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/token``

* Start Authorization Code Grant flow.   
  __GET__  ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/authorize``

* Introspect (validate) issued JWTs as specified [here](https://tools.ietf.org/html/rfc7662).  
  __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/introspect``

* Revoke issued JWTs.  
  __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/revoke``

* Redirects to *redirect_uri* in Authorization Code Grant flow and issues access_code token.  
  __GET__  ``http://localhost:8080/services/authentication/login``

