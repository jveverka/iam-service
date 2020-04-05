## Client Credentials Flow
[RFC reference](https://tools.ietf.org/html/rfc6749#section-1.3.4)

![flow](134_client-credentials-flow.svg)

* __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/token``
  ``grant_type=client_credentials&scope=<scopes>&client_id=<id>&client_secret=<secret>``

### Test in Postman
![postman](134_flow-postman.png)

### Test in Insomnia
![insomnia](134_flow-insomnia.png)
