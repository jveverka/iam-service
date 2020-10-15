package itx.iamservice.server.services.impl;

import itx.iamservice.client.JWTUtils;
import itx.iamservice.client.dto.StandardTokenClaims;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.Permission;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.services.ProviderConfigurationService;
import itx.iamservice.server.services.IAMSecurityException;
import itx.iamservice.server.services.IAMSecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.security.interfaces.RSAKey;
import java.util.Optional;
import java.util.Set;

import static itx.iamservice.core.ModelCommons.ADMIN_PROJECT_SET;


@Service
public class IAMSecurityValidatorImpl implements IAMSecurityValidator {

    private static final Logger LOG = LoggerFactory.getLogger(IAMSecurityValidatorImpl.class);

    private final ProviderConfigurationService providerConfigurationService;

    public IAMSecurityValidatorImpl(@Autowired ProviderConfigurationService providerConfigurationService) {
        this.providerConfigurationService = providerConfigurationService;
    }

    @Override
    public void validate(String authorization) throws IAMSecurityException {
        JWToken token = JWTUtils.extractJwtToken(authorization);
        Optional<StandardTokenClaims> tokenClaimsOptional = JWTUtils.getClaimsFromToken(token);
        if (tokenClaimsOptional.isPresent()) {
            StandardTokenClaims standardTokenClaims = tokenClaimsOptional.get();
            validatePermissions(standardTokenClaims.getIssuerUri(), standardTokenClaims.getOrganizationId(), standardTokenClaims.getProjectId(), ADMIN_PROJECT_SET, Set.of(), standardTokenClaims, token);
        } else {
            throw new IAMSecurityException("Authorization token validation has failed.");
        }
    }

    private void validatePermissions(URI issuerUri, OrganizationId organizationId, ProjectId projectId, Set<Permission> requiredAdminScopes, Set<Permission> requiredApplicationScopes, StandardTokenClaims standardTokenClaims, JWToken token) throws IAMSecurityException {
        LOG.info("validate: {}", organizationId.getId());
        Optional<RSAKey> keyById;
        if (projectId != null) {
            keyById = providerConfigurationService.getKeyById(organizationId, projectId, standardTokenClaims.getKeyId());
        } else {
            keyById = providerConfigurationService.getKeyById(organizationId, standardTokenClaims.getKeyId());
        }
        if (keyById.isPresent()) {
            boolean result = JWTUtils.validateToken(keyById.get(), issuerUri, projectId, requiredAdminScopes, requiredApplicationScopes, token);
            if (!result) {
                throw new IAMSecurityException("Authorization token validation has failed: token is invalid!");
            }
        } else {
            throw new IAMSecurityException("Authorization token validation has failed: key not found !");
        }
    }

}
