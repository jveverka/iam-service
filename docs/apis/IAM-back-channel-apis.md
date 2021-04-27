## Back Channel REST APIs

* Provider configuration as required by [RFC8414](https://tools.ietf.org/html/rfc8414#section-3), 
  [OpenID](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) or 
  [Provider Meta-Data](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata)  
  __GET__ ``http://localhost:8080/services/oauth2/{organization-id}/{project-id}/.well-known/openid-configuration``

* Provider configuration __jwks_uri__ as specified by [RFC7517](https://tools.ietf.org/html/rfc7517) JSON Web Key (JWK).  
  __GET__ ``http://localhost:8080/services/oauth2/{organization-id}/{project-id}/.well-known/jwks.json``

* UserInfo verification endpoint as specified in [OIDC core 1.0](https://openid.net/specs/openid-connect-core-1_0.html#UserInfoRequest)   
  __GET__ ``http://localhost:8080/services/oauth2/{organization-id}/{project-id}/userinfo``

* Introspect (validate) issued JWTs as specified [here](https://tools.ietf.org/html/rfc7662).  
  __POST__ ``http://localhost:8080/services/oauth2/{organization-id}/{project-id}/introspect``

### Service discovery

* Get IAM-service instance info.  
  __GET__ ``http://localhost:8080/services/discovery/build-info``
  
* Get all organizations managed by this instance of IAM-service.  
  __GET__ ``http://localhost:8080/services/discovery``

* Get organization by ID managed by this instance of IAM-service.  
  __GET__ ``http://localhost:8080/services/discovery/{organization-id}``

* Get project within organization.  
  __GET__ ``http://localhost:8080/services/discovery/{organization-id}/{project-id}``

* Get user info for organization and project and user with unique id provided.  
  __GET__ ``http://localhost:8080/services/discovery/{organization-id}/{project-id}/users/{user-id}``

* Get client info for organization and project and client with unique id provided.  
  __GET__ ``http://localhost:8080/services/discovery/{organization-id}/{project-id}/clients/{client-id}``
