## Authorization and Authentication REST APIs

* Provider configuration as required by [RFC8414](https://tools.ietf.org/html/rfc8414#section-3), 
  [OpenID](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) or 
  [Provider Meta-Data](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata)
  Base url in this case is ``http://localhost:8080/services/authentication/{organization-id}/{project-id}``  
  __GET__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/.well-known/openid-configuration``

* Provider configuration __jwks_uri__ as specified by [RFC7517](https://tools.ietf.org/html/rfc7517) JSON Web Key (JWK).
  __GET__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/.well-known/jwks.json

* __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/token``
* __GET__  ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/auth``
* __GET__  ``http://localhost:8080/services/authentication/login``
* __POST__ ``http://localhost:8080/services/tokens/{organization-id}/{project-id}/verify``
* __POST__ ``http://localhost:8080/services/tokens/{organization-id}/{project-id}/revoke``
