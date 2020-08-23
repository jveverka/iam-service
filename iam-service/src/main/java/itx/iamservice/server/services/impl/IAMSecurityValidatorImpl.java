package itx.iamservice.server.services.impl;

import itx.iamservice.client.IAMClient;
import itx.iamservice.client.JWTUtils;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.server.services.IAMSecurityException;
import itx.iamservice.server.services.IAMSecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class IAMSecurityValidatorImpl implements IAMSecurityValidator {

    private static final Logger LOG = LoggerFactory.getLogger(IAMSecurityValidatorImpl.class);

    private final IAMClient iamClient;

    public IAMSecurityValidatorImpl(@Autowired IAMClient iamClient) {
        this.iamClient = iamClient;
    }

    @Override
    public void validate(OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredAdminScopes, Set<Permission> requiredApplicationScopes, String authorization) throws IAMSecurityException {
        LOG.info("validate: {}/{} {}", organizationId.getId(), projectId.getId());
        JWToken token = JWTUtils.extractJwtToken(authorization);
        boolean result = iamClient.validate(organizationId, projectId, requiredAdminScopes, requiredApplicationScopes, token);
        if (!result) {
            throw new IAMSecurityException("Authorization token validation has failed.");
        }
    }

    @Override
    public void validate(OrganizationId organizationId, Set<Permission> requiredAdminScopes, Set<Permission> requiredApplicationScopes, String authorization) throws IAMSecurityException {
        LOG.info("validate: {} {}", organizationId.getId());
        JWToken token = JWTUtils.extractJwtToken(authorization);
        boolean result = iamClient.validate(organizationId, null, requiredAdminScopes, requiredApplicationScopes, token);
        if (!result) {
            throw new IAMSecurityException("Authorization token validation has failed.");
        }
    }

}
