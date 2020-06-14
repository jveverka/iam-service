## JWT mappings

### Header  
* __typ__ - JWT
* __kid__ - (Key ID) unique identifier of X.509 certificate 
  containing public key for JWT signature verification. 
* __alg__ - RS256 

### Claims: Access_Token, Refresh_Token
* __iss__ (issuer) - OrganizationId, string. 
* __aud__ (audience) - ProjectId or project audience, string array.
* __sub__ (subject) - UserId or ClientId, string.
* __exp__ (Expiration Time) = iat + session duration, datetime+timezone, string
* __nbf__ (Not Before) = iat, datetime+timezone, string
* __iat__ (Issued At) = current datetime+timezone, string
* __jti__ (JWT ID) - unique id, random UUID string.
* __typ__ - toke type: __Bearer__ | __Refresh__
* __permissions__ - granted subject permissions, string array of permissionsId(s) for subject. 
* __scope__ - enumeration of following values: [ openid | permissions ] *TDB 

### Claims: ID_Token
* __iss__ (issuer) - OrganizationId, string. 
* __aud__ (audience) - ClientId, string.
* __sub__ (subject) - Combination of <OrganizationId/ProjectId/[UserId|ClientId]>, string.
* __exp__ (Expiration Time) = iat + session duration, datetime+timezone, string
* __iat__ (Issued At) - current datetime+timezone, string
* __auth_time__ - timestamp of client's authentication.
* __nonce__ - (Nonce) - optional nonce value provided by client. 

### JWT signature 
Issued tokens are always signed using private key of issuer.
__kid__ in JWT header must be used to get X.509 certificate via 
back channel for JWT signature verification.
 
#### References
* __Standard Claims__ - Data model mapping of [RFC7519 registered JWT claim names](https://tools.ietf.org/html/rfc7519#section-4):
* Data model mapping of [OpenID connect standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims):
* Scope - [oauth scopes](https://oauth.net/2/scope/).
