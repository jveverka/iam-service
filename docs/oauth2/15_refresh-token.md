## Refresh Token
[RFC reference](https://tools.ietf.org/html/rfc6749#section-1.5)
* __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/token``
  ``grant_type=refresh_token&refresh_token=<refresh_token>&scope=<scope>&client_id=<client_id>&client_secret=<client_secret>``

### Test in Insomnia
![insomnia](15_flow-insomnia.png) 
