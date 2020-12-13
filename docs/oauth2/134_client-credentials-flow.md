## Client Credentials Flow
[RFC reference](https://tools.ietf.org/html/rfc6749#section-1.3.4)

![flow](134_client-credentials-flow.svg)

* Get tokens using Client Credentials Flow. 
  ```
  curl --location --request POST 'http://localhost:8080/services/oauth2/{organization-id}/{project-id}/token?grant_type=client_credentials&scope={scopes}&client_id={id}&client_secret={secret}' \
  --header 'Content-Type: application/x-www-form-urlencoded'   
  ```

1. Client application sends __POST__ request above with client credentials.
2. iam-service verifies organization / project, client credentials.
3. iam-service issues tokens if verification above is successful.
4. tokens are provided to client application.
5. [Token Verification process, back channel](token-verification-back-channel.md).   
6. client application use access_token to access resources.
7. [Refresh Tokens flow](15_refresh-tokens-flow.md).

### Test in Postman or Insomnia
| Name                 | Value                                                                   |
|----------------------|-------------------------------------------------------------------------|
| __Grant Type__       | Client Credentials                                                      | 
| __Access Token URL__ | ```http://localhost:8080/services/oauth2/iam-admins/iam-admins/token``` |
| __Client ID__        | admin-client                                                            | 
| __Client Secret__    | top-secret                                                              | 
| __Scope__            | ""                                                                      |  
