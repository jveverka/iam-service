package itx.iamservice.server.services.impl;

import itx.iamservice.client.JWTUtils;
import itx.iamservice.client.dto.StandardTokenClaims;
import itx.iamservice.client.impl.KeyProvider;
import itx.iamservice.core.model.JWToken;
import itx.iamservice.core.services.ProviderConfigurationService;
import itx.iamservice.server.services.IAMSecurityException;
import itx.iamservice.server.services.IAMSecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
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
    public StandardTokenClaims validate(String authorization) throws IAMSecurityException {
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
            StandardTokenClaims standardTokenClaims = tokenClaimsOptional.get();
            boolean result = JWTUtils.validatePermissions(standardTokenClaims, ADMIN_PROJECT_SET, Set.of());
            if (!result) {
                throw new IAMSecurityException("Authorization token validation has failed: token is invalid!");
            }
            return standardTokenClaims;
        } else {
            throw new IAMSecurityException("Authorization token validation has failed.");
        }
    }

}
