## Password Credentials Flow
[RFC reference](https://tools.ietf.org/html/rfc6749#section-1.3.3)
* __POST__ ``http://localhost:8080/services/authentication/{organization-id}/{project-id}/token``
  ``grant_type=password&username=<username>&password=<password>&scope=<scopes>&client_id=<id>&client_secret=<secret>``

### Test in Postman
![postman](133_flow-postman.png)

### Test in Insomnia
![insomnia](133_flow-insomnia.png)
