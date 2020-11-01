package one.microproject.iamservice.server.services.impl;

import one.microproject.iamservice.client.JWTUtils;
import one.microproject.iamservice.client.dto.StandardTokenClaims;
import one.microproject.iamservice.client.impl.KeyProvider;
import one.microproject.iamservice.client.spring.AuthenticationImpl;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.services.ProviderConfigurationService;
import one.microproject.iamservice.server.services.IAMSecurityException;
import one.microproject.iamservice.server.services.IAMSecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Optional;
import java.util.Set;

import static one.microproject.iamservice.core.ModelCommons.ADMIN_PROJECT_SET;
import static one.microproject.iamservice.core.ModelCommons.verifyProjectAdminPermissions;


@Service
public class IAMSecurityValidatorImpl implements IAMSecurityValidator {

    private static final Logger LOG = LoggerFactory.getLogger(IAMSecurityValidatorImpl.class);

    private final ProviderConfigurationService providerConfigurationService;

    public IAMSecurityValidatorImpl(@Autowired ProviderConfigurationService providerConfigurationService) {
        this.providerConfigurationService = providerConfigurationService;
    }

    @Override
    public StandardTokenClaims verifyAdminAccess(String authorization) throws IAMSecurityException {
        StandardTokenClaims standardTokenClaims = verifyToken(authorization);
        boolean result = JWTUtils.validatePermissions(standardTokenClaims, ADMIN_PROJECT_SET, Set.of());
        if (!result) {
            throw new IAMSecurityException("Authorization token validation has failed: token is invalid!");
        }
        return standardTokenClaims;
    }

    @Override
    public StandardTokenClaims verifyToken(String authorization) throws IAMSecurityException {
        JWToken token = JWTUtils.extractJwtToken(authorization);
        KeyProvider provider = keyId -> {
            Optional<PublicKey> key = providerConfigurationService.getKeyById(keyId);
            if (key.isPresent()) {
                return key.get();
            } else {
                throw new IAMSecurityException("JWT signing key not found !");
            }
        };
        Optional<StandardTokenClaims> tokenClaimsOptional = JWTUtils.validateToken(provider, token);
        if (tokenClaimsOptional.isPresent()) {
            return tokenClaimsOptional.get();
        } else {
            throw new IAMSecurityException("Authorization token validation has failed.");
        }
    }

    @Override
    public void verifyProjectAdminAccess(OrganizationId organizationId, ProjectId projectId) throws IAMSecurityException {
        AuthenticationImpl authentication = (AuthenticationImpl)SecurityContextHolder.getContext().getAuthentication();
        StandardTokenClaims standardTokenClaims = (StandardTokenClaims)authentication.getDetails();
        LOG.info("JWT iss: {}", standardTokenClaims.getIssuer());
        if (!verifyProjectAdminPermissions(organizationId, projectId, standardTokenClaims.getScope())) {
            throw new IAMSecurityException("Authorization token validation has failed.");
        }
    }

}
