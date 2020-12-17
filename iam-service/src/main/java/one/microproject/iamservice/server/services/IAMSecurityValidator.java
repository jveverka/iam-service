package one.microproject.iamservice.server.services;

import one.microproject.iamservice.core.dto.StandardTokenClaims;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.model.UserId;

public interface IAMSecurityValidator {

    /**
     * Validate Global Admin's authentication.
     * @param authorization - http 'Authentication' header. Expected format: "Bearer [JWT]".
     * @return {@link StandardTokenClaims} - decoded JWT token claims.
     * @throws IAMSecurityException - thrown in case that token validation fails or insufficient permissions.
     */
    StandardTokenClaims verifyGlobalAdminAccess(String authorization) throws IAMSecurityException;

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
     * @param organizationId - unique organization ID
     * @param projectId - unique project ID
     * @throws IAMSecurityException - thrown in case that token validation fails or insufficient permissions.
     */
    void verifyProjectAdminAccess(OrganizationId organizationId, ProjectId projectId) throws IAMSecurityException;

    /**
     * Verify access for user which belongs to project and organization.
     * @param organizationId - unique organization ID
     * @param projectId - unique project ID
     * @param userId - unique user ID
     * @throws IAMSecurityException - thrown in case that token validation fails or organization/project/user does not match.
     */
    void verifyUserAccess(OrganizationId organizationId, ProjectId projectId, UserId userId) throws IAMSecurityException;

}
