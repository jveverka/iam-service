## Getting Admin Access

In order to start using *iam-service* you have to get access as priviledged 
"admin" user which belongs to dedicated *iam-admins* organization *and iam-admins* project.
This special project will be called __iam-admins__ project. Default login credentials and settings:

| Parameter              | Value                                                                 | 
|------------------------|-----------------------------------------------------------------------|
| Organization name      | iam-admins                                                            |
| Project name           | iam-admins                                                            |
| admin username         | admin                                                                 |
| admin password         | [see config manual](01a-standalone-server-config.md)                  |
| admin client id        | admin-client                                                          |
| admin client secret    | [see config manual](01a-standalone-server-config.md)                  |
| Supported OAuth2 flows | Authorization Code, Password Credentials                              |
| Authorization url      | http://localhost:8080/services/oauth2/iam-admins/iam-admins/authorize |
| Access token url       | http://localhost:8080/services/oauth2/iam-admins/iam-admins/token     |
| Redirect URL           | http://localhost:8080/services/oauth2/iam-admins/iam-admins/redirect  |

To get 'admin' access tokens, please [use one of supported](README.md) OAuth2 flows. 
Once you get access tokens for privileged 'admin' user, you can use iam-service to 
create your own organizations and projects with users and clients.
See also: [Default Access Rules](Default-Access-Configuration.md) and [Terms and Vocabulary](Terms-and-Vocabulary.md)

* Get Admin Access tokens  
  ```
  curl --location --request POST 'http://localhost:8080/services/oauth2/iam-admins/iam-admins/token?grant_type=password&username=admin&password=secret&scope=&client_id=admin-client&client_secret=top-secret' \
  --header 'Content-Type: application/x-www-form-urlencoded' 
  ```


* [next step: Create new Organizations and Projects](02b-create-organization-with-admin.md)  
