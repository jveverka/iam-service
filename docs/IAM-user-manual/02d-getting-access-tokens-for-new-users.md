# Getting Access Tokens for new Users.
At this point, new Organization / Project with clients and users is fully setup.
You may start using supported OAuth2 flows for authorizations and authentication.
[See also: Terms and Vocabulary](Terms-and-Vocabulary.md)

### How to get Users's Access and Refresh Tokens
```
#template:
#curl --location --request POST 'http://localhost:8080/services/oauth2/{organization-id}/{project-id}/token?grant_type=password&username={username}&password={password}&scope=&client_id={client-is}&client_secret={client-secret}' \
curl --location --request POST 'http://localhost:8080/services/oauth2/test-org-001/project-001/token?grant_type=password&username=user-001&password=secret&scope=&client_id=client-002&client_secret=secret' \
--header 'Content-Type: application/x-www-form-urlencoded'
```

### How to refresh Users's Tokens
```
#template:
curl --location --request POST 'http://localhost:8080/services/oauth2/{organization-id}/{project-id}/token?grant_type=refresh_token&refresh_token={refresh_token}&client_id={client_id}&client_secret={client_secret}' \
--header 'Content-Type: application/x-www-form-urlencoded'
```

### How to get Clients's Access and Refresh Tokens
```
#template:
#curl --location --request POST 'http://localhost:8080/services/oauth2/{organization-id}/{project-id}/token?grant_type=client_credentials&client_id={client-is}&client_secret={client-secret}' \
curl --location --request POST 'http://localhost:8080/services/oauth2/test-org-001/project-001/token?grant_type=client_credentials&client_id=client-002&client_secret=secret' \
--header 'Content-Type: application/x-www-form-urlencoded'
```

* [next step: Examples - How to secure your microservices](../../iam-examples)
* [Terms and Vocabulary](Terms-and-Vocabulary.md)
* [Security Model](IAM-Service-Security-Model.md)
