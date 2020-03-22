### JWT mappings
Data model is mapped to registered [JWT claim names](https://tools.ietf.org/html/rfc7519#section-4) according rules below:
* __iss__ (issuer) - OrganizationId, string. 
* __aud__  (audience) - ProjectId, string.
* __subj__ (subject) - ClientId, string.

Remaining registered [JWT claim names](https://tools.ietf.org/html/rfc7519#section-4) are used as following:
* __exp__ (Expiration Time) = iat + session duration, datetime+timezone, string
* __nbf__ (Not Before) = iat, datetime+timezone, string
* __iat__ (Issued At) = current datetime+timezone, string
* __jti__ (JWT ID) - unique id, random uuid string.

Non-registered mappings:
* __roles__ - string array of roleId(s) for subject. 
