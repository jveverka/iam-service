package itx.iamservice.server.services;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;

import java.util.Set;

public interface IAMSecurityValidator {

    void validate(OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredAdminScopes, Set<Permission> requiredApplicationScopes, String authentication) throws IAMSecurityException;

    void validate(OrganizationId organizationId, Set<Permission> requiredAdminScopes, Set<Permission> requiredApplicationScopes, String authentication) throws IAMSecurityException;

}
