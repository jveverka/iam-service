package itx.iamservice.server.services;

import itx.iamservice.client.dto.StandardTokenClaims;

public interface IAMSecurityValidator {

    /**
     * Validate Admin's authentication.
     * @param authorization - http 'Authentication' header. Expected format: "Bearer [JWT]".
     * @return {@link StandardTokenClaims} - decoded JWT token claims.
     * @throws IAMSecurityException - thrown in case that token validation fails.
     */
    StandardTokenClaims validate(String authorization) throws IAMSecurityException;

}
