## Authorization Code Flow
[RFC reference](https://tools.ietf.org/html/rfc6749#section-1.3.1)

![flow](131_authorization-code-flow.svg)

1. User initializes authentication and authorization flow.
2. Client sends auth request, response_type=code.
3. Request is verified by IAM-service.
4. IAM-service responds, providing login context.
5. User enters login credentials.
6. Client sends login user's login credentials to IAM-service.
7. IAM-service verifies login credentials and responds with scope data for user.
8. Client received available scopes / permissions and presents consent screen to user.
9. Selects scope for this auth action and confirm.
10. Client sends back list of approved scopes. 
11. Approved scopes are evaluated and authorization code is issued.
12. Client is redirected to Callback URL handing over code.
13. Resource server sends code to IAM-service.   
14. IAM-service issues tokens and sends back access_token and refresh_token.
15. access_token and refresh_token are forwarded to client.
16. Login flow is finished.
17. Access resources using issued access_token.

### Test in Browser
* Init login flow using web browser.
  ```
  http://localhost:8080/services/authentication/iam-admins/iam-admins/authorize?response_type=code&state=123444&client_id=admin-client&scope=&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fservices%2Fauthentication%2Fiam-admins%2Fiam-admins%2Fredirect
  ``` 

### Test in Postman
| Name                 | Value                                                                               |
|----------------------|-------------------------------------------------------------------------------------|
| __Grant Type__       | Authorization Code                                                                  |
| __Callback URL__     | ```http://localhost:8080/services/authentication/iam-admins/iam-admins/redirect```  |
| __Auth URL__         | ```http://localhost:8080/services/authentication/iam-admins/iam-admins/authorize``` |
| __Access Token URL__ | ```http://localhost:8080/services/authentication/iam-admins/iam-admins/token```     |
| __Client ID__        |  admin-client                                                                       |
| __Client Secret__    |  top-secret                                                                         |
| __Scope__            |  ""                                                                                 |
| __State__            | <random-string>                                                                     |

![postman](131_flow-postman-02.png) 
![postman](131_flow-postman-03.png) 
![postman](131_flow-postman-04.png)

### Test in Insomnia
![insomnia](131_flow-insomnia-02.png) 
![insomnia](131_flow-insomnia-03.png) 
![insomnia](131_flow-insomnia-04.png)