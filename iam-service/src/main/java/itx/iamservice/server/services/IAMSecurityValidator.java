package itx.iamservice.server.services;

import itx.iamservice.client.dto.StandardTokenClaims;

public interface IAMSecurityValidator {

    /**
     * Validate Admin's authentication.
     * @param authorization - http 'Authentication' header. Expected format: "Bearer [JWT]".
     * @return {@link StandardTokenClaims} - decoded JWT token claims.
     * @throws IAMSecurityException - thrown in case that token validation fails.
     */
    StandardTokenClaims verifyAdminAccess(String authorization) throws IAMSecurityException;

    /**
     * Verify JWT token properties:
     * - signature
     * - expiration time
     * @param authorization - http 'Authentication' header. Expected format: "Bearer [JWT]".
     * @return - decoded JWT token claims.
     * @throws IAMSecurityException - thrown in case that token validation fails.
     */
    StandardTokenClaims verifyToken(String authorization) throws IAMSecurityException;

}
