# Getting Access Tokens for new Users.
At this point, new Organization / Project with clients and users is fully setup.
You may start using supported OAuth2 flows for authorizations and authentication.
[See also: Terms and Vocabulary](Terms-and-Vocabulary.md)

### How to get Users's Access and Refresh Tokens
```
curl --location --request POST 'http://localhost:8080/services/authentication/{organization-id}/{project-id}/token?grant_type=password&username={username}&password={password}&scope=&client_id={client-is}&client_secret={client-secret}'
```

### How to get Clients's Access and Refresh Tokens
```
curl --location --request POST 'http://localhost:8080/services/authentication/{organization-id}/{project-id}/token?grant_type=client_credentials&client_id={client-is}&client_secret={client-secret}'
```

* [next step: Examples - How to secure your microservices](../../iam-examples)
