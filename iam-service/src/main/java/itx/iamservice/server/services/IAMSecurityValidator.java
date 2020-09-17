package itx.iamservice.server.services;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;

import java.net.URI;
import java.util.Set;

public interface IAMSecurityValidator {

    void validate(URI issuerUri, OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredAdminScopes, Set<Permission> requiredApplicationScopes, String authentication) throws IAMSecurityException;

    void validate(Set<Permission> requiredAdminScopes, Set<Permission> requiredApplicationScopes, String authentication) throws IAMSecurityException;

}
