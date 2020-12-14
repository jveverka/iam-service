## Authorization Code Flow (With PKCE)
[RFC reference](https://tools.ietf.org/html/rfc6749#section-1.3.1)

![flow](131_authorization-code-flow.svg)

1. User initializes authentication and authorization flow.
2. Client sends auth request, response_type=code. *PKCE1*  
   __GET__ ``/authorize``
3. Request is verified by IAM-service.
4. IAM-service responds, providing login context.
5. User enters login credentials.
6. Client sends login user's login credentials to IAM-service.  
   __POST__ ``/authorize``
7. IAM-service verifies login credentials and responds with scope data for user.
8. Client received available scopes / permissions and presents consent screen to user.
9. Selects scope for this auth action and confirm.
10. Client sends back list of approved scopes.   
    __POST__ ``/consent``
11. Approved scopes are evaluated and authorization code is issued.
12. Client is redirected to Callback URL handing over code.
13. Resource server sends code to IAM-service. *PKCE2*  
    __POST__ ``/token``
14. IAM-service issues tokens and sends back access_token and refresh_token.
15. access_token and refresh_token are forwarded to client.
16. Login flow is finished.
17. [Token Verification process, back channel](token-verification-back-channel.md). 
18. Access resources using issued access_token.
19. [Refresh Tokens flow](15_refresh-tokens-flow.md).

### PKCE fow extension
* __PKCE1__ - Client sends Authorization Request with code_challenge and code_challenge_method 
  as specified in [RFC7636](https://tools.ietf.org/html/rfc7636).
* __PKCE2__ - Authorization Code is send together with original code_verifier [RFC7636](https://tools.ietf.org/html/rfc7636). 

### Test in Browser
* Init login flow using web browser.
  ```
  curl --location --request GET 'http://localhost:8080/services/oauth2/{organization-id}/{project-id}/authorize?response_type=code&state={state}&client_id={client-id}&client_secret={client-secret}&scope=&redirect_uri={redirect-uri}'
  ``` 

### Test in Postman
| Name                         | Value                                                                       |
|------------------------------|-----------------------------------------------------------------------------|
| __Grant Type__               | Authorization Code (With PKCE**)                                                         |
| __Callback URL__             | ```http://localhost:8080/services/oauth2/iam-admins/iam-admins/redirect```  |
| __Auth URL__                 | ```http://localhost:8080/services/oauth2/iam-admins/iam-admins/authorize``` |
| __Access Token URL__         | ```http://localhost:8080/services/oauth2/iam-admins/iam-admins/token```     |
| __Client ID__                | admin-client                                                                |
| __Client Secret__            | top-secret                                                                  |
| __Code Challenge Method **__ | SHA-256 or Plain                                                            | 
| __Code Verifier  **__        | <random-high-entropy-string>                                                |
| __Scope__                    | ""                                                                          |
| __State__                    | <random-string>                                                             |

** Only for flow with PKCE

![postman](131_flow-postman-02.png) 
![postman](131_flow-postman-03.png) 
![postman](131_flow-postman-04.png)

### Test in Insomnia
![insomnia](131_flow-insomnia-02.png) 
![insomnia](131_flow-insomnia-03.png) 
![insomnia](131_flow-insomnia-04.png)