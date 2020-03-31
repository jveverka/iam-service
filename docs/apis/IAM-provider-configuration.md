# IAM Provider Configuration

* Provider configuration as required by [RFC8414](https://tools.ietf.org/html/rfc8414#section-3), 
  [OpenID](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) or 
  [Provider Meta-Data](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata)
  Base url in this case is ``http://localhost:8080/config/{organization-id}/{project-id}``  
  __GET__ ``http://localhost:8080/config/{organization-id}/{project-id}/.well-known/openid-configuration``
