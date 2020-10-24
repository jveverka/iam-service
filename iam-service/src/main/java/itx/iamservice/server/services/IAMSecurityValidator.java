package itx.iamservice.server.services;

import itx.iamservice.client.dto.StandardTokenClaims;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

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

    /**
     * Verify access for project Administrator. Security context is checked.
     * @param organizationId
     * @param projectId
     */
    void verifyProjectAdminAccess(OrganizationId organizationId, ProjectId projectId);

}
