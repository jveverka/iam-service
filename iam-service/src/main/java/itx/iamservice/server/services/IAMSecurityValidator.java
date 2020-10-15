package itx.iamservice.server.services;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;

import java.net.URI;
import java.util.Set;

public interface IAMSecurityValidator {

    @Deprecated
    void validate(URI issuerUri, OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredAdminScopes, Set<Permission> requiredApplicationScopes, String authorization) throws IAMSecurityException;

    @Deprecated
    void validate(Set<Permission> requiredAdminScopes, Set<Permission> requiredApplicationScopes, String authorization) throws IAMSecurityException;

    /**
     * Validate Admin's authentication.
     * @param authorization - http 'Authentication' header. Expected format: "Bearer [JWT]".
     * @throws IAMSecurityException
     */
    void validate(String authorization) throws IAMSecurityException;

}
