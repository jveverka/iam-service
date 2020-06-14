## JWT mappings

### Header  
* __typ__ - JWT
* __kid__ - (Key ID) unique identifier of X.509 certificate 
  containing public key for JWT signature verification. 
* __alg__ - RS256 

### Claims: Access_Token
Data model mapping of [RFC7519 registered JWT claim names](https://tools.ietf.org/html/rfc7519#section-4):
* __iss__ (issuer) - OrganizationId, string. 
* __aud__ (audience) - ProjectId, string.
* __sub__ (subject) - UserId or ClientId, string.
* __exp__ (Expiration Time) = iat + session duration, datetime+timezone, string
* __nbf__ (Not Before) = iat, datetime+timezone, string
* __iat__ (Issued At) = current datetime+timezone, string
* __jti__ (JWT ID) - unique id, random UUID string.

Data model mapping of [OpenID connect standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims):
* NA

Non-registered claim mappings:
* __typ__ - toke type: __Bearer__
* __permissions__ - subject permissions, string array of permissionsId(s) for subject. 
* __scope__ - enumeration of following values: [ openid | permissions ]

### Claims: Refresh_Token
* __typ__ - toke type: __Refresh__
* TBD

### Claims: ID_Token
* __aud__ (audience) - ProjectId, string.
* __sub__ (subject) - UserId or ClientId, string.
* __exp__ (Expiration Time) = iat + session duration, datetime+timezone, string
* __iat__ (Issued At) = current datetime+timezone, string
* __auth_time__ - timestamp of client's authentication.

### JWT signature 
Issued tokens are always signed using private key of issuer.
__kid__ in JWT header must be used to get X.509 certificate via 
back channel for JWT signature verification.
 
