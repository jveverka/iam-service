## Password Credentials Flow
[RFC reference](https://tools.ietf.org/html/rfc6749#section-1.3.3)

![flow](133_password-credentials-flow.svg)

* __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/token``
  ``grant_type=password&username=<username>&password=<password>&scope=<scopes>&client_id=<id>&client_secret=<secret>``

### Test in Postman or Insomnia
| Name                 | Value                                                                               |
|----------------------|-------------------------------------------------------------------------------------|
| __Grant Type__       | Password Credentials                                                                | 
| __Access Token URL__ | ```http://localhost:8080/services/authentication/iam-admins/iam-admins/token```     |
| __Username__         | admin                                                                               | 
| __Password__         | secret                                                                              | 
| __Client ID__        | admin-client                                                                        | 
| __Client Secret__    | top-secret                                                                          | 
| __Scope__            | ""                                                                                  |  
