## Client Credentials Flow
[RFC reference](https://tools.ietf.org/html/rfc6749#section-1.3.4)

![flow](134_client-credentials-flow.svg)

* __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/token``
  ``grant_type=client_credentials&scope=<scopes>&client_id=<id>&client_secret=<secret>``

### Test in Postman or Insomnia
| Name                 | Value                                                                               |
|----------------------|-------------------------------------------------------------------------------------|
| __Grant Type__       | Client Credentials                                                                  | 
| __Access Token URL__ | ```http://localhost:8080/services/authentication/iam-admins/iam-admins/token```     |
| __Client ID__        | admin-client                                                                        | 
| __Client Secret__    | top-secret                                                                          | 
| __Scope__            | ""                                                                                  |  
